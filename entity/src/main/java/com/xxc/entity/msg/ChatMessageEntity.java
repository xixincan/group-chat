package com.xxc.entity.msg;

import java.io.Serializable;

/**
 * 客户端服务交互的聊天消息实体类
 *
 * @author xixincan
 * 2020-03-12
 * @version 1.0.0
 */
public class ChatMessageEntity implements Serializable {

    //消息类型
    private Integer type;
    //发送者标识
    private String sourceUid;
    //消息内容
    private String content;
    //消息接收者用户标识（私聊）
    private String targetUid;
    //消息接收群标识（群聊）
    private Integer targetGid;
    //文件大小（传文件）
    private Long fileSize;
    //文件名称（传文件）
    private String fileName;
    //文件地址（传文件）
    private String fileURL;

    public Integer getType() {
        return type;
    }

    public ChatMessageEntity setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getSourceUid() {
        return sourceUid;
    }

    public ChatMessageEntity setSourceUid(String sourceUid) {
        this.sourceUid = sourceUid;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ChatMessageEntity setContent(String content) {
        this.content = content;
        return this;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public ChatMessageEntity setTargetUid(String targetUid) {
        this.targetUid = targetUid;
        return this;
    }

    public Integer getTargetGid() {
        return targetGid;
    }

    public ChatMessageEntity setTargetGid(Integer targetGid) {
        this.targetGid = targetGid;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public ChatMessageEntity setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public ChatMessageEntity setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileURL() {
        return fileURL;
    }

    public ChatMessageEntity setFileURL(String fileURL) {
        this.fileURL = fileURL;
        return this;
    }
}
