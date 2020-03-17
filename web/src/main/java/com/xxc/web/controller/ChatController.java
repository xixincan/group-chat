package com.xxc.web.controller;

import com.xxc.common.consts.ConfigKey;
import com.xxc.entity.response.FileInfo;
import com.xxc.entity.result.MyResult;
import com.xxc.service.IConfigService;
import com.xxc.service.IFileUploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    @Resource
    private IFileUploadService fileUploadService;

    @PostMapping("groupchat/path")
    public MyResult<String> getWebSocketURI() {
        return MyResult.success(
                this.configService.getValue(ConfigKey.CHAT_WS_HOST) + ":" +
                        this.configService.getValue(ConfigKey.CHAT_WS_PORT) +
                        this.configService.getValue(ConfigKey.CHAT_WS_URI)
        );
    }

    @PostMapping("file/upload")
    public MyResult<FileInfo> upload(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        return MyResult.success(this.fileUploadService.upload(file, request));
    }

}
