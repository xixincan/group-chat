package com.xxc.service.impl;

import cn.hutool.log.StaticLog;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xxc.common.cache.RedisService;
import com.xxc.entity.msg.ChatMessageEntity;
import com.xxc.service.IChatService;
import com.xxc.service.IConfigService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
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

    @Resource
    private RedisService redisService;
    @Resource
    private UserService userService;
    @Resource
    private IConfigService configService;

    @Override
    public void handleBinding(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        String uid = messageEntity.getSourceUid();
        Channel channel = ctx.channel();
        StaticLog.info("接收到来自用户:{}的连接注册消息，绑定到channel:{}", messageEntity.getSourceUid(), channel.id());
        BINDING_TABLE.forcePut(uid, channel);
        //todo 将用户状态修改为在线状态
    }

    @Override
    public void handleSingleSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        //TODO
    }

    @Override
    public void handlerGroupSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity) {
        //TODO
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
        //TODO
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
}
