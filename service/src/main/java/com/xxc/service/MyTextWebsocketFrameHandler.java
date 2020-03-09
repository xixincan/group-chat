package com.xxc.service;

import com.xxc.common.util.Member;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

//这里TextWebsocketFram类型，表示一个文本帧（frame）
@ChannelHandler.Sharable
public class MyTextWebsocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTextWebsocketFrameHandler.class);

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息：" + msg.text());
        LOGGER.info("-服务器收到消息:{}", msg.text());
        System.out.println(channelGroup.size());
        //回复消息
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(Member.REG_TAB.get(ctx.channel()) +
                        " " + LocalDateTime.now() + "\n" + msg.text())
        );
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String ip = Member.getIP(ctx.channel().remoteAddress().toString());
        try {
            Member.checkIP(ip);
        } catch (Exception e) {
            ctx.channel().writeAndFlush(e.getMessage());
            throw e;
        }
        Member.REG_TAB.putIfAbsent(ctx.channel(), Member.getNickname(ip));
        if (!Member.REG_TAB.keySet().contains(ctx.channel())) {
            channelGroup.writeAndFlush(new TextWebSocketFrame("\t\t" + Member.getNickname(ip) + "加入了群聊."));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        //asLongText是唯一的id
        System.out.println("handlerAdded被调用" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded被调用" + ctx.channel().id().asShortText());
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Member.REG_TAB.remove(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved" + ctx.channel().id().asLongText());
        channelGroup.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exp发生：" + cause.getMessage());
        LOGGER.error("catch exp:" + cause.getMessage());
        Member.REG_TAB.remove(ctx.channel());
        ctx.close();
    }
}
