package com.xxc.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisTool;
import com.xxc.common.consts.RedisKey;
import com.xxc.common.util.MyTicketUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xixincan
 * 2020-03-21
 * @version 1.0.0
 */
@Service
@ChannelHandler.Sharable
public class TicketFilterHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private RedisTool redisTool;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            StaticLog.info("进行验证客户端连接聊天服务的请求;URI={}", fullHttpRequest.uri());
            String ticket = MyTicketUtil.getTicket(fullHttpRequest);
            if (StrUtil.isEmpty(ticket)) {
                StaticLog.warn("客户端没有通过验证，ticket=null");
                ctx.close();
                return;
            }
            if (!this.redisTool.exist(RedisKey.USER_DIR + MyTicketUtil.getUid(ticket))) {
                StaticLog.warn("客户端没有通过验证，ticket校验不通过");
                ctx.close();
            }
            StaticLog.info("客户端通过ticket校验");
        }
        super.channelRead(ctx, msg);
    }
}
