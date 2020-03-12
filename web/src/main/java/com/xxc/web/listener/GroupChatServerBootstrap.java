package com.xxc.web.listener;

import cn.hutool.log.StaticLog;
import com.xxc.common.consts.ConfigKey;
import com.xxc.core.GroupChatClientInitHandler;
import com.xxc.service.IConfigService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * 群聊启动器
 *
 * Created by xixincan
 * 2020-03-08
 * @version 1.0.0
 */
@Component
public class GroupChatServerBootstrap {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture serverChannelFuture;

    @Resource
    private IConfigService configService;
    @Resource
    private GroupChatClientInitHandler groupChatClientInitHandler;

    @PostConstruct
    public void init() {
        //使用单独的线程去启动
        CompletableFuture.runAsync(() -> {
            StaticLog.info("====>>>>开始启动群聊服务<<<<======");
            this.startServer();
        });
    }

    private void startServer() {
        this.bossGroup = new NioEventLoopGroup(1);
        //CPU core * 2
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap
                    //一主多从
                    .group(this.bossGroup, this.workerGroup)
                    //选择NioServer
                    .channel(NioServerSocketChannel.class)
                    //配置TCP参数，握手字符串长度设置
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //TCP_NODELAY算法，尽可能发送大块数据，减少充斥的小块数据
                    .option(ChannelOption.TCP_NODELAY, true)
                    //Netty日志
                    .handler(new LoggingHandler(LogLevel.WARN))
                    //开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //配置固定长度接收缓存区分配器
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))
                    //自定义消息处理器
                    .childHandler(this.groupChatClientInitHandler);

            this.serverChannelFuture = serverBootstrap.bind(this.configService.getIntegerValue(ConfigKey.CHAT_WS_PORT)).sync();

            this.serverChannelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    StaticLog.info("======>>>>>聊天服务启动成功<<<<<=======");
                }
            });

            //监听channel关闭
//            this.serverChannelFuture.channel().closeFuture().sync();
        } catch (Exception exp) {
            StaticLog.error("聊天服务遇到问题关闭服务:{}:{}", exp.getStackTrace(), exp);
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void close() {
        this.serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
            StaticLog.warn("====>>>>群聊服务关闭<<<<======");
        } catch (InterruptedException ie) {
            StaticLog.error("群聊服务关闭遇到问题:{}:{}", ie.getStackTrace(), ie);
        }
    }

}