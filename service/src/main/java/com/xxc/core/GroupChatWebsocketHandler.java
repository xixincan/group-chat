package com.xxc.core;

import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisService;
import com.xxc.common.util.Member;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.exp.AccessException;
import com.xxc.service.IChatService;
import com.xxc.service.IIpPlanService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 生命周期：
 *      handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()
 * handlerAdded()：ch.pipeline().addLast(this)之后的回调，表示在当前的 channel 中，已经成功添加了一个 handler 处理器。
 * channelRegistered()：表示当前的 channel 的所有的逻辑处理已经和某个NioEventLoop中线程建立了绑定关系
 * channelActive()：channel 的所有的业务逻辑链准备完毕
 * channelRead()：客户端向服务端发来数据，每次都会回调此方法，表示有数据可读
 * channelReadComplete()：表示数据读取完毕。
 *
 *      channelInactive() -> channelUnregistered() -> handlerRemoved()
 * channelInactive()：表面这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
 * channelUnregistered()：表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
 * handlerRemoved()：这条连接上添加的所有的业务逻辑处理器都给移除掉。
 *
 * 其中：
 * handlerAdded() 与 handlerRemoved()
 * 这两个方法通常可以用在一些资源的申请和释放
 * channelActive() 与 channelInActive()
 * 对我们的应用程序来说，这两个方法表明的含义是 TCP 连接的建立与释放，
 * 通常我们在这两个回调里面统计单机的连接数，channelActive() 被调用，连接数加1，channelInActive() 被调用，连接数减1
 * 另外，我们也可以在 channelActive() 方法中，实现对客户端连接 ip 黑白名单的过滤
 *
 * TextWebSocketFrame是我们唯一真正需要处理的帧类型
 *
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
@ChannelHandler.Sharable
public class GroupChatWebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private IIpPlanService ipPlanService;
    @Resource
    private RedisService redisService;
    @Resource
    private IChatService chatService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //todo 申请相关的资源
        //asLongText是唯一的id
        StaticLog.debug("handlerAdded被调用.shortChannelId={};longChannelId={}", ctx.channel().id().asShortText(), ctx.channel().id().asLongText());
        channelGroup.add(ctx.channel());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        //todo 打印日志，该方法不做处理
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //todo 根据IP计划执行IP校验，通过后计入在线名单，统计在线人数, 初始化客户端状态
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //这里TextWebsocketFrame类型，表示一个文本帧（frame）
        //todo 解析消息类型，并将消息分门别类交给不同的接口进行相应的处理
//        StaticLog.info("-服务器收到消息:{}", msg.text());
        //回复消息
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(Member.REG_TAB.get(ctx.channel()) +
                        " " + LocalDateTime.now() + "\n" + msg)
        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //todo 修正在线人数统计，下线客户端状态
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //todo 记录日志，什么也不做
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //todo 释放为该channel申请的资源
        StaticLog.debug("handlerRemoved; longChannelId={}", ctx.channel().id().asLongText());
        channelGroup.remove(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //todo 关注客户端空闲状态，进行状态变更
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        StaticLog.error("catch exp:" + cause.getMessage());
        Member.REG_TAB.remove(ctx.channel());
        ctx.close();
    }
}
