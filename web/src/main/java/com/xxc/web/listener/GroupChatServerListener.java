package com.xxc.web.listener;

import cn.hutool.log.StaticLog;
import com.xxc.core.GroupChatBootstrap;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 群聊启动器
 * <p>
 * Created by xixincan
 * 2020-03-08
 *
 * @version 1.0.0
 */
@Component
public class GroupChatServerListener implements ApplicationListener<ContextRefreshedEvent> {

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Resource
    private GroupChatBootstrap groupChatBootstrap;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (this.running.compareAndSet(false, true)) {
            this.initResources();
            //使用单独的线程去启动
            CompletableFuture.runAsync(() -> {
                StaticLog.info("====>>>>开始启动群聊服务<<<<======");
                this.groupChatBootstrap.startServer();
            });
        }
    }

    private void initResources() {
        StaticLog.info("------>>>开始初始化服务相关资源<<<-------");
        //todo 初始化zk集群 以支持服务集群部署
    }

}