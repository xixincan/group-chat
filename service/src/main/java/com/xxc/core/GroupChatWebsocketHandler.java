package com.xxc.core;

import cn.hutool.log.StaticLog;
import com.xxc.common.util.Member;
import com.xxc.common.util.MyIPUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@ChannelHandler.Sharable
//这里TextWebsocketFram类型，表示一个文本帧（frame）
public class GroupChatWebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        StaticLog.info("-服务器收到消息:{}", msg.text());
        //回复消息
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(Member.REG_TAB.get(ctx.channel()) +
                        " " + LocalDateTime.now() + "\n" + msg.text())
        );
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String ip = MyIPUtil.getIP(ctx.channel().remoteAddress().toString());
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
        StaticLog.debug("handlerAdded被调用.shortChannelId={};longChannelId={}", ctx.channel().id().asShortText(), ctx.channel().id().asLongText());
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Member.REG_TAB.remove(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        StaticLog.debug("handlerRemoved; longChannelId={}", ctx.channel().id().asLongText());
        channelGroup.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        StaticLog.error("catch exp:" + cause.getMessage());
        Member.REG_TAB.remove(ctx.channel());
        ctx.close();
    }
}
