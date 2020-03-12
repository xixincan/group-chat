package com.xxc.web.controller;

import com.xxc.common.consts.ConfigKey;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author xixincan
 * 2020-03-12
 * @version 1.0.0
 */
@RestController
@RequestMapping("chat")
public class ChatController {

    @Resource
    private IConfigService configService;

    @PostMapping("groupchat/path")
    public MyResult<String> getWebSocketURI() {
        return MyResult.success(
                this.configService.getValue(ConfigKey.CHAT_WS_HOST) + ":" +
                        this.configService.getValue(ConfigKey.CHAT_WS_PORT) +
                        this.configService.getValue(ConfigKey.CHAT_WS_URI)
        );
    }

}
