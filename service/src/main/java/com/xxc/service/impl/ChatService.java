package com.xxc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.util.EncryptUtil;
import com.xxc.common.util.MyIPUtil;
import com.xxc.core.GlobalGroupChatContext;
import com.xxc.dao.mapper.MsgLogMapper;
import com.xxc.dao.mapper.UserMsgMapper;
import com.xxc.dao.model.MsgLog;
import com.xxc.dao.model.UserMsg;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.model.ChatMessageEntity;
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
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class ChatService implements IChatService {

    private static final ExecutorService CHAT =
            new ThreadPoolExecutor(16, 64, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024));

    private static final ExecutorService TASK =
            new ThreadPoolExecutor(16, 32, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024));

    @Resource
    private GlobalGroupChatContext groupChatContext;
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
        this.groupChatContext.bind(uid, channel);
        //todo 将用户状态修改为在线状态

        //todo 检测该用户是否有{ConfigKey.MSG_DELAY_HOUR}内未接收的消息

    }

    @Override
    public void handleSingleSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        String targetUid = messageEntity.getTargetUid();
        if (StrUtil.isEmpty(targetUid)) {
            StaticLog.error("无效的用户ID:{}", targetUid);
            throw new AccessException("该用户不存在！");
        }
        //获取该用户并将消息写入到对应的channel
        //在线注册表中找不到对应的用户，首先需要确定用户是否是真实存在，如果是离线用户，则将消息记录到数据表中延迟发送
        boolean delay = false;
        if (this.groupChatContext.isOnLocalBinding(targetUid)) {
            //直接写入对应的channel
            messageEntity.setTimestamp(DateUtil.formatDateTime(DateUtil.date()));
            this.sendMessage(this.groupChatContext.getLocalChannel(targetUid), messageEntity);
        } else if (this.groupChatContext.isOnBinding(targetUid)) {
            //todo 集群通知


        } else if (this.userService.checkUser(targetUid)) {
            //离线状态
            delay = true;
            messageEntity.setTimestamp(DateUtil.formatDateTime(DateUtil.date()));
            StaticLog.warn("用户[{}]不在线, 消息将延迟发送", messageEntity.getTargetUid());
        } else {
            //用户不存在或被封
            StaticLog.error("无效的用户ID:{}", targetUid);
            throw new AccessException("该用户不存在！");

        }
        String channelRemoteIP = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        this.recordSingleMsgAsync(messageEntity, channelRemoteIP, delay);
    }

    @Override
    public void handlerGroupSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        Integer targetGid = messageEntity.getTargetGid();
        if (null == targetGid || targetGid <= 0) {
            StaticLog.error("无效的群ID:{}", targetGid);
            throw new AccessException("该群不存在！");
        }
        //获取该群组的所有成员并将消息写入到对应的channel
        GroupInfo groupInfo = this.groupService.getGroupInfo(targetGid);
        if (null == groupInfo) {
            StaticLog.warn("没有找到对应的群:gid={}", targetGid);
            throw new AccessException("该群不存在！");
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
            if (this.groupChatContext.isOnLocalBinding(item)) {
                final Channel channel = this.groupChatContext.getLocalChannel(item);
                futureList.add(
                        CompletableFuture.runAsync(() -> this.sendMessage(channel, messageEntity), CHAT)
                );
                sentList.add(item);
            } else if (this.groupChatContext.isOnBinding(item)) {
                //todo 集群通知


            } else if (this.userService.checkUser(item)) {
                //记录消息未发送的成员；待上线后发送
                unsentList.add(item);
            } else {
                //用户不存在或被封
                StaticLog.error("无效的用户ID:{}", item);
            }
        });

        String channelRemoteIP = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        this.recordGroupMsgAsync(messageEntity, sentList, unsentList, channelRemoteIP);
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
        String remove = this.groupChatContext.unbind(ctx.channel());
        StaticLog.info("用户[{}]下线了", remove);
    }

    private void sendMessage(Channel channel, ChatMessageEntity message) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(message)));
    }

    private void recordSingleMsgAsync(ChatMessageEntity messageEntity, String channelRemoteIP, boolean delay) {
        CompletableFuture.runAsync(() -> {
            String mid = EncryptUtil.genRandomID();
            this.recordMessage(messageEntity, channelRemoteIP, mid);
            UserMsg userMsg = new UserMsg();
            userMsg.setMid(mid);
            userMsg.setUid(messageEntity.getTargetUid());
            userMsg.setSent(!delay);
            this.userMsgMapper.insertSelective(userMsg);
        }, TASK);
    }

    private void recordGroupMsgAsync(ChatMessageEntity messageEntity,
                                     List<String> sentUidList,
                                     List<String> unsentUidList,
                                     String channelRemoteIP) {
        CompletableFuture.runAsync(() -> {
            String mid = EncryptUtil.genRandomID();
            this.recordMessage(messageEntity, channelRemoteIP, mid);
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

    private void recordMessage(ChatMessageEntity messageEntity, String channelRemoteIP, String mid) {
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
    }

}
