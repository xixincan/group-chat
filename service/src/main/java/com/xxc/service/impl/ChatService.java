package com.xxc.service.impl;

import com.xxc.service.IChatService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class ChatService implements IChatService {


    @Override
    public void handleRegister(ChannelHandlerContext ctx, String frameText) {
        //TODO
    }

    @Override
    public void handleSingleSendMsg(ChannelHandlerContext ctx, String frameText) {
        //TODO
    }

    @Override
    public void handlerGroupSendMsg(ChannelHandlerContext ctx, String frameText) {
        //TODO
    }

    @Override
    public void handleSingleSendFile(ChannelHandlerContext ctx, String frameText) {
        //TODO
    }

    @Override
    public void handleGroupSendFile(ChannelHandlerContext ctx, String frameText) {
        //TODO
    }

    @Override
    public void handleExit(ChannelHandlerContext ctx) {
        //TODO
    }

    @Override
    public void handleDismatchType(ChannelHandlerContext ctx) {
        //TODO
    }
}
