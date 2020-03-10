package com.xxc.web.listener;

import cn.hutool.log.StaticLog;
import com.xxc.web.chatserver.GroupChatServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author xixincan
 * 2020-03-09
 * @version 1.0.0
 */
@Component
public class GroupChatBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private GroupChatServer groupChatServer;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        StaticLog.info("开始启动群聊服务.....");
        //必须使用单独的线程去启动
        CompletableFuture.runAsync(() -> {
            try {
                this.groupChatServer.start();
            } catch (InterruptedException e) {
                StaticLog.error("启动失败", e);
            }
        });
    }
}
