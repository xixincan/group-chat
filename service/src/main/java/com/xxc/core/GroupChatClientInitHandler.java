package com.xxc.core;

import com.xxc.common.consts.ConfigKey;
import com.xxc.service.IConfigService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class GroupChatClientInitHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private IConfigService configService;
    @Resource
    private GroupChatWebsocketHandler groupChatWebsocketHandler;

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     */
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        //因为基于http协议，使用http的编码和解码
        pipeline.addLast(new HttpServerCodec());
        //是以块的方式写位，添加chunkedWriterHandler; 方便大文件传输，不过实质上都是短的文本数据
        pipeline.addLast(new ChunkedWriteHandler());
        // 说明： http数据在传输的过程中是分段，HttpObjectAggregator就是可以将多个分段聚合
        // 这就是为什么当浏览器发送大量的数据时会发出多个http请求
        // 把HTTP头、HTTP体拼成完整的HTTP请求
        pipeline.addLast(new HttpObjectAggregator(65536));
        //对应websocket，它的数据是以帧frame的形式传递
        // 可以看到WebsocketFrame下面有6个子类
        // 浏览器请求时：ws://localhost:7000/hello 表示请求的uri
        // WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议，保持长连接
        pipeline.addLast(new WebSocketServerProtocolHandler(this.configService.getValue(ConfigKey.CHAT_WS_URI)));
        //Netty提供的处理空闲状态的处理器，1没读2没写3没有读写 就会发送一个心跳检测包金策是否连接
        //当idleStateEvent触发后，就会传递给管道的下一个handler去处理；通过调用下一个handler的userEventTriggered
        //有可能： 读空闲、写空闲、读写空闲
        pipeline.addLast(new IdleStateHandler(
                this.configService.getIntegerValue(ConfigKey.READ_IDLE),
                this.configService.getIntegerValue(ConfigKey.WRITE_IDLE),
                this.configService.getIntegerValue(ConfigKey.ALL_IDLE)
        ));
        //自定义的处理器
        pipeline.addLast(this.groupChatWebsocketHandler);
    }
}
