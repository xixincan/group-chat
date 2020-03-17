package com.xxc.service;

import com.xxc.entity.model.ChatMessageEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.List;

/**
 * 消息服务
 *
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public interface IChatService {

    void handleBinding(ChannelHandlerContext ctx, ChatMessageEntity messageEntity);

    void handleSingleSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity);

    void handlerGroupSendMsg(ChannelHandlerContext ctx, ChatMessageEntity messageEntity);

    void handleSingleSendFile(ChannelHandlerContext ctx, ChatMessageEntity messageEntity);

    void handleGroupSendFile(ChannelHandlerContext ctx, ChatMessageEntity messageEntity);

    void handleExit(ChannelHandlerContext ctx);

}
