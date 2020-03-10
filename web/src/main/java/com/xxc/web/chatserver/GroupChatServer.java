package com.xxc.web.chatserver;

import cn.hutool.log.StaticLog;
import com.xxc.core.GroupChatTextWebsocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * Created by xixincan
 * 2020-03-08
 * @version 1.0.0
 */
@Service
public class GroupChatServer {

    @Value("${group-chat.bind.port}")
    private int port;

    @Resource
    private GroupChatTextWebsocketFrameHandler groupChatTextWebsocketFrameHandler;

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //CPU core * 2
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //因为基于http协议，使用http的编码和解码
                            pipeline.addLast(new HttpServerCodec());
                            //是以块的方式写位，添加chunkedWriterHandler
                            pipeline.addLast(new ChunkedWriteHandler());
                            //说明： http数据在传输的过程中是分段，HttpObjectAggregator就是可以将多个分段聚合
                            // 这就是为什么当浏览器发送大量的数据时会发出多个http请求
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            //对应websocket，它的数据是以帧frame的形式传递
                            // 可以看到WebsocketFrame下面有6个子类
                            // 浏览器请求时：ws://localhost:7000/hello 表示请求的uri
                            // WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议，保持长连接
                            pipeline.addLast(new WebSocketServerProtocolHandler("/groupchat"));
                            //自定义的处理器
                            pipeline.addLast(groupChatTextWebsocketFrameHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();

            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    StaticLog.info("======>>>>>聊天服务已启动<<<<<=======");
                }
            });

            //监听channel关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}