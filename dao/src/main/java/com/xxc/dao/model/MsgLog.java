package com.xxc.dao.model;

import com.xxc.dao.base.DynamicTableNameBean;
import com.xxc.entity.annotation.MultiTable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "msg_log")
public class MsgLog extends DynamicTableNameBean implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 消息ID
     */
    @MultiTable(4)
    @Column(name = "mid")
    private String mid;

    /**
     * 消息类型
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 发送者
     */
    @Column(name = "sourceUid")
    private String sourceUid;

    /**
     * 用户
     */
    @Column(name = "targetUid")
    private String targetUid;

    /**
     * 群标识
     */
    @Column(name = "gid")
    private Integer gid;

    /**
     * 文件名
     */
    @Column(name = "fileName")
    private String fileName;

    /**
     * 文件大小
     */
    @Column(name = "fileSize")
    private String fileSize;

    /**
     * 文件地址
     */
    @Column(name = "fileURL")
    private String fileURL;

    /**
     * IP地址
     */
    @Column(name = "ipAddr")
    private String ipAddr;

    /**
     * 消息的时间毫秒
     */
    @Column(name = "time")
    private Long time;

    @Column(name = "created")
    private Date created;

    /**
     * 消息
     */
    @Column(name = "content")
    private String content;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取消息ID
     *
     * @return mid - 消息ID
     */
    public String getMid() {
        return mid;
    }

    /**
     * 设置消息ID
     *
     * @param mid 消息ID
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * 获取消息类型
     *
     * @return type - 消息类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置消息类型
     *
     * @param type 消息类型
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取发送者
     *
     * @return sourceUid - 发送者
     */
    public String getSourceUid() {
        return sourceUid;
    }

    /**
     * 设置发送者
     *
     * @param sourceUid 发送者
     */
    public void setSourceUid(String sourceUid) {
        this.sourceUid = sourceUid;
    }

    /**
     * 获取用户
     *
     * @return targetUid - 用户
     */
    public String getTargetUid() {
        return targetUid;
    }

    /**
     * 设置用户
     *
     * @param targetUid 用户
     */
    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }

    /**
     * 获取群标识
     *
     * @return gid - 群标识
     */
    public Integer getGid() {
        return gid;
    }

    /**
     * 设置群标识
     *
     * @param gid 群标识
     */
    public void setGid(Integer gid) {
        this.gid = gid;
    }

    /**
     * 获取文件名
     *
     * @return fileName - 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名
     *
     * @param fileName 文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件大小
     *
     * @return fileSize - 文件大小
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * 设置文件大小
     *
     * @param fileSize 文件大小
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取文件地址
     *
     * @return fileURL - 文件地址
     */
    public String getFileURL() {
        return fileURL;
    }

    /**
     * 设置文件地址
     *
     * @param fileURL 文件地址
     */
    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    /**
     * 获取IP地址
     *
     * @return ipAddr - IP地址
     */
    public String getIpAddr() {
        return ipAddr;
    }

    /**
     * 设置IP地址
     *
     * @param ipAddr IP地址
     */
    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    /**
     * 获取消息的时间毫秒
     *
     * @return time - 消息的时间毫秒
     */
    public Long getTime() {
        return time;
    }

    /**
     * 设置消息的时间毫秒
     *
     * @param time 消息的时间毫秒
     */
    public void setTime(Long time) {
        this.time = time;
    }

    /**
     * @return created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 获取消息
     *
     * @return content - 消息
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息
     *
     * @param content 消息
     */
    public void setContent(String content) {
        this.content = content;
    }
}