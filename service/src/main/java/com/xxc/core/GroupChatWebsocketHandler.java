package com.xxc.core;

import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisService;
import com.xxc.common.util.Member;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.exp.AccessException;
import com.xxc.service.IChatService;
import com.xxc.service.IIpPlanService;
import com.xxc.service.impl.ChatService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@ChannelHandler.Sharable
public class GroupChatWebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private IIpPlanService ipPlanService;
    @Resource
    private RedisService redisService;
    @Resource
    private IChatService chatService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        String ipAddr = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        //根据config表中配置的IP执行拒绝计划
        if (!this.ipPlanService.checkIpAddr(ipAddr)) {
            throw new AccessException("Access Denied!");
        }
//        Member.REG_TAB.putIfAbsent(ctx.channel(), Member.getNickname(ip));
        if (!Member.REG_TAB.keySet().contains(ctx.channel())) {
//            channelGroup.writeAndFlush(new TextWebSocketFrame("\t\t" + Member.getNickname(ip) + "加入了群聊."));
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        //asLongText是唯一的id
        StaticLog.debug("handlerAdded被调用.shortChannelId={};longChannelId={}", ctx.channel().id().asShortText(), ctx.channel().id().asLongText());
        channelGroup.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        //这里TextWebsocketFrame类型，表示一个文本帧（frame）

//        StaticLog.info("-服务器收到消息:{}", msg.text());
        //回复消息
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(Member.REG_TAB.get(ctx.channel()) +
                        " " + LocalDateTime.now() + "\n" + msg)
        );
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //将event向下转型
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String eventType;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                default:
                    eventType = "未知空闲";
                    break;
            }
            StaticLog.info("{}-超时事件--{}", ctx.channel().remoteAddress(), eventType);
        }
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
