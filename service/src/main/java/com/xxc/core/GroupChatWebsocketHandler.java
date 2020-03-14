package com.xxc.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.consts.RedisKey;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.enums.ChatTypeEnum;
import com.xxc.entity.exp.AccessException;
import com.xxc.entity.msg.ChatMessageEntity;
import com.xxc.service.IChatService;
import com.xxc.service.IIpPlanService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 生命周期：
 * handlerAdded() -> channelRegistered() -> channelActive() -> channelRead() -> channelReadComplete()
 * handlerAdded()：ch.pipeline().addLast(this)之后的回调，表示在当前的 channel 中，已经成功添加了一个 handler 处理器。
 * channelRegistered()：表示当前的 channel 的所有的逻辑处理已经和某个NioEventLoop中线程建立了绑定关系
 * channelActive()：channel 的所有的业务逻辑链准备完毕
 * channelRead()：客户端向服务端发来数据，每次都会回调此方法，表示有数据可读
 * channelReadComplete()：表示数据读取完毕。
 * <p>
 * channelInactive() -> channelUnregistered() -> handlerRemoved()
 * channelInactive()：表面这条连接已经被关闭了，这条连接在 TCP 层面已经不再是 ESTABLISH 状态了
 * channelUnregistered()：表明与这条连接对应的 NIO 线程移除掉对这条连接的处理
 * handlerRemoved()：这条连接上添加的所有的业务逻辑处理器都给移除掉。
 * <p>
 * 其中：
 * handlerAdded() 与 handlerRemoved()
 * 这两个方法通常可以用在一些资源的申请和释放
 * channelActive() 与 channelInActive()
 * 对我们的应用程序来说，这两个方法表明的含义是 TCP 连接的建立与释放，
 * 通常我们在这两个回调里面统计单机的连接数，channelActive() 被调用，连接数加1，channelInActive() 被调用，连接数减1
 * 另外，我们也可以在 channelActive() 方法中，实现对客户端连接 ip 黑白名单的过滤
 * <p>
 * TextWebSocketFrame是我们唯一真正需要处理的帧类型
 *
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
@ChannelHandler.Sharable
public class GroupChatWebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

//    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Resource
    private IIpPlanService ipPlanService;
    @Resource
    private RedisTool redisTool;
    @Resource
    private IChatService chatService;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //todo 申请相关的资源
        //asLongText是唯一的id
        StaticLog.debug("handlerAdded被调用.shortChannelId={};longChannelId={}", ctx.channel().id().asShortText(), ctx.channel().id().asLongText());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        //打印日志，该方法不做处理
        StaticLog.info("一个客户端进入连接channelID={}", ctx.channel().id().asLongText());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //根据IP计划执行IP校验，通过后计入在线名单，统计在线人数, 客户端必须在初次连接之后自动发送注册的消息才能确定用户身份。
        //所以在这里做不了初始化客户端的工作
        String ipAddr = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        //根据config表中配置的IP执行拒绝计划
        if (!this.ipPlanService.checkIpAddr(ipAddr)) {
            this.redisTool.incrementAndGet(RedisKey.REFUSE_COUNT, 1);
            throw new AccessException("Access Denied!");
        }
        Long online = this.redisTool.incrementAndGet(RedisKey.ONLINE_COUNT, 1);
        StaticLog.info("客户端{}成功连接; 当前在线人数:{}", ipAddr, online);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //这里TextWebsocketFrame类型，表示一个文本帧（frame）
        //解析消息类型，并将消息分门别类交给不同的接口进行相应的处理
        String text = msg.text();
        ChatMessageEntity messageEntity = JSONUtil.toBean(text, ChatMessageEntity.class);
        if (null == messageEntity || StrUtil.isEmpty(messageEntity.getSourceUid())) {
            StaticLog.error("未知的消息，无法处理:{}", text);
            return;
        }
        StaticLog.info("服务器收到来自{}消息并对消息类型识别分发", messageEntity.getSourceUid());
        switch (ChatTypeEnum.find(messageEntity.getType())) {
            case BINDING:
                this.chatService.handleBinding(ctx, messageEntity);
                break;
            case GROUP_SENDING:
                this.chatService.handlerGroupSendMsg(ctx, messageEntity);
                break;
            case SINGLE_SENDING:
                this.chatService.handleSingleSendMsg(ctx, messageEntity);
                break;
            case FILE_MSG_GROUP_SENDING:
                this.chatService.handleGroupSendFile(ctx, messageEntity);
                break;
            case FILE_MSG_SINGLE_SENDING:
                this.chatService.handleSingleSendFile(ctx, messageEntity);
                break;
            case ERROR:
                StaticLog.error("错误的消息类型，无法处理:{}", text);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long online = this.redisTool.incrementAndGet(RedisKey.ONLINE_COUNT, -1);
        this.chatService.handleExit(ctx);
        String ipAddr = MyIPUtil.getChannelRemoteIP(ctx.channel().remoteAddress().toString());
        StaticLog.info("客户端{}下线; 当前在线人数:{}", ipAddr, online);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //记录日志，什么也不做
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //todo 释放为该channel申请的资源
        StaticLog.debug("handlerRemoved; longChannelId={}", ctx.channel().id().asLongText());
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
                    //todo 将用户状态修改为LEAVE状态
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    //todo 将用户状态修改为BUSY状态
                    break;
                default:
                    eventType = "读写空闲";
                    //todo 将用户状态修改为SLEEP状态
                    break;
            }
            StaticLog.info("{}-{}超时事件--{}",
                    ctx.channel().remoteAddress(), this.chatService.getBindUid(ctx.channel()), eventType);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        StaticLog.error("catch exp:{}", cause.getMessage());
        StaticLog.error(cause);
        //抓取到异常情况, 关闭客户端的连接，
        ctx.close();
        //todo 并清空用户状态
    }
}
