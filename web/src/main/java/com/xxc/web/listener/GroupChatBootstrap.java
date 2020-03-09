package com.xxc.web.listener;

import com.xxc.core.GroupChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xixincan
 * 2020-03-09
 * @version 1.0.0
 */
@Component
public class GroupChatBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupChatBootstrap.class);

    @Resource
    private GroupChatServer groupChatServer;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOGGER.info("开始启动群聊服务.....");
        //必须使用单独的线程去启动
        new Thread(() ->{
            try {
                this.groupChatServer.start();
            } catch (InterruptedException e) {
                LOGGER.error("启动失败", e);
            }
        }).start();
    }
}
