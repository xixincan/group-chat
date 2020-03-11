package com.xxc.service;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息服务
 *
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public interface IChatService {

    void handleRegister(ChannelHandlerContext ctx, String frameText);

    void handleSingleSendMsg(ChannelHandlerContext ctx, String frameText);

    void handlerGroupSendMsg(ChannelHandlerContext ctx, String frameText);

    void handleSingleSendFile(ChannelHandlerContext ctx, String frameText);

    void handleGroupSendFile(ChannelHandlerContext ctx, String frameText);

    void handleExit(ChannelHandlerContext ctx);

    void handleDismatchType(ChannelHandlerContext ctx);

}
