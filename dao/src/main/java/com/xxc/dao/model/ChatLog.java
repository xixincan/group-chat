package com.xxc.dao.model;

import com.xxc.dao.base.DynamicTableNameBean;
import com.xxc.entity.annotation.MultiTable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "chat_log")
public class ChatLog extends DynamicTableNameBean implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 群标识
     */
    @Column(name = "gid")
    @MultiTable(4)
    private Integer gid;

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
    @Column(name = "message")
    private String message;

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
     * 获取用户
     *
     * @return uid - 用户
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置用户
     *
     * @param uid 用户
     */
    public void setUid(String uid) {
        this.uid = uid;
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
     * @return message - 消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置消息
     *
     * @param message 消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
}