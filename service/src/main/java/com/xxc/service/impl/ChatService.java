package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyIPUtil;
import com.xxc.dao.mapper.MsgLogMapper;
import com.xxc.dao.mapper.UserMapper;
import com.xxc.dao.mapper.UserMsgMapper;
import com.xxc.dao.model.MsgLog;
import com.xxc.dao.model.UserMsg;
import com.xxc.entity.msg.ChatMessageEntity;
import com.xxc.entity.response.GroupInfo;
import com.xxc.entity.response.UserInfo;
import com.xxc.service.IChatService;
import com.xxc.service.IConfigService;
import com.xxc.service.IGroupService;
import com.xxc.service.IUserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class ChatService implements IChatService {

    //用户与客户端连接的双向注册表 uid <--> channel
    private static final BiMap<String, Channel> BINDING_TABLE = HashBiMap.create();

    private static final ExecutorService POOL = new ThreadPoolExecutor(16, 64, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(65536));

    private static final ExecutorService TASK = new ThreadPoolExecutor(16, 64, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(65536));

    @Resource
    private RedisTool redisTool;
    @Resource
    private IUserService userService;
    @Resource
    private IGroupService groupService;
    @Resource
    private IConfigService configService;
    @Resource
    private MsgLogMapper msgLogMapper;
    @Resource
    private UserMsgMapper userMsgMapper;

    @Override
    public void handleBinding(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        String uid = messageEntity.getSourceUid();
        Channel channel = ctx.channel();
        StaticLog.info("接收到来自用户:{}的连接注册消息，绑定到channel:{}", messageEntity.getSourceUid(), channel.id());
        BINDING_TABLE.forcePut(uid, channel);
        //todo 将用户状态修改为在线状态
        //todo 检测该用户是否有未接收的消息
    }

    @Override
    public void handleSingleSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        //TODO
    }

    @Override
    public void handlerGroupSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        Integer targetGid = messageEntity.getTargetGid();
        if (null == targetGid || targetGid <= 0) {
            StaticLog.warn("无效的群ID:{}", targetGid);
            return;
        }
        //获取该群组的所有成员并将消息写入到对应的channel
        GroupInfo groupInfo = this.groupService.getGroupInfo(targetGid);
        if (null == groupInfo) {
            StaticLog.warn("没有找到对应的群:gid={}", targetGid);
            return;
        }
        StaticLog.info("用户[{}]在群[{}]发送了消息:{}", messageEntity.getSourceUid(), groupInfo.getGroupId(), messageEntity.getContent());

        List<String> uidList = groupInfo.getMembers().stream().map(UserInfo::getUid).collect(Collectors.toList());
        uidList.remove(messageEntity.getSourceUid());
        List<CompletableFuture> futureList = new ArrayList<>();
        DateTime now = DateUtil.date();
        messageEntity.setTimestamp(DateUtil.formatDateTime(now));
        final List<String> sentList = new ArrayList<>();
        final List<String> unsentList = new ArrayList<>();
        uidList.forEach(item -> {
            final Channel channel = BINDING_TABLE.get(item);
            if (null != channel) {
                futureList.add(
                        CompletableFuture.runAsync(() -> sendMessage(channel, messageEntity), POOL)
                );
                sentList.add(item);
            } else {
                //记录消息未发送的成员；待上线后发送
                unsentList.add(item);
            }
        });

        String channelRemoteIP = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        this.recordMsgAsync(messageEntity, sentList, unsentList, channelRemoteIP);
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).join();
    }

    @Override
    public void handleSingleSendFile(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        //TODO
    }

    @Override
    public void handleGroupSendFile(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        //TODO
    }

    @Override
    public void handleExit(ChannelHandlerContext ctx) {
        String remove = BINDING_TABLE.inverse().remove(ctx.channel());
        StaticLog.info("用户[{}]下线了", remove);
    }

    @Override
    public Channel getTargetChannel(String targetUid) {
        return BINDING_TABLE.get(targetUid);
    }

    @Override
    public List<Channel> getTargetChannelGroup(Collection<String> uidCollection) {
        return uidCollection.parallelStream()
                .map(BINDING_TABLE::get)
                .collect(Collectors.toList());
    }

    @Override
    public String getBindUid(Channel channel) {
        return BINDING_TABLE.inverse().get(channel);
    }

    private void sendMessage(Channel channel, ChatMessageEntity message) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(message)));
    }

    private void recordMsgAsync(ChatMessageEntity messageEntity,
                                List<String> sentUidList,
                                List<String> unsentUidList,
                                String channelRemoteIP) {
        CompletableFuture.runAsync(() -> {
            String mid = EncryptUtil.genRandomID();
            MsgLog record = new MsgLog();
            record.setMid(mid);
            record.setType(messageEntity.getType());
            record.setSourceUid(messageEntity.getSourceUid());
            record.setTargetUid(messageEntity.getTargetUid());
            record.setGid(messageEntity.getTargetGid());
            record.setContent(messageEntity.getContent());
            record.setFileName(messageEntity.getFileName());
            record.setFileSize(messageEntity.getFileSize());
            record.setFileURL(messageEntity.getFileURL());
            record.setTime(DateUtil.parse(messageEntity.getTimestamp(), DatePattern.NORM_DATETIME_PATTERN).getTime());
            record.setIpAddr(channelRemoteIP);

            this.msgLogMapper.insertSelective(record);
            UserMsg userMsg = new UserMsg();
            userMsg.setMid(mid);
            if (CollectionUtil.isNotEmpty(sentUidList)) {
                userMsg.setSent(Boolean.TRUE);
                sentUidList.forEach(item -> {
                    userMsg.setId(null);
                    userMsg.setUid(item);
                    userMsg.setCreated(null);
                    this.userMsgMapper.insertSelective(userMsg);
                });
            }
            if (CollectionUtil.isNotEmpty(unsentUidList)) {
                StaticLog.info("[{}]用户未上线，消息[{}]将延迟发送", unsentUidList.size(), mid);
                userMsg.setSent(Boolean.FALSE);
                unsentUidList.forEach(item -> {
                    userMsg.setId(null);
                    userMsg.setUid(item);
                    userMsg.setCreated(null);
                    this.userMsgMapper.insertSelective(userMsg);
                });
            }
        }, TASK);
    }

}
