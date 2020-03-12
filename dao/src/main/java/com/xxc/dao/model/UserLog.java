package com.xxc.dao.model;

import com.xxc.dao.base.DynamicTableNameBean;
import com.xxc.entity.annotation.MultiTable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user_log")
public class UserLog extends DynamicTableNameBean implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * UID
     */
    @Column(name = "uid")
    @MultiTable(4)
    private String uid;

    /**
     * 事件0-注册1-登录2-退出-1-注销
     */
    @Column(name = "event")
    private Byte event;

    /**
     * IP地址
     */
    @Column(name = "ipAddr")
    private String ipAddr;

    @Column(name = "created")
    private Date created;

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
     * 获取UID
     *
     * @return uid - UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置UID
     *
     * @param uid UID
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取事件0-注册1-登录2-退出-1-注销
     *
     * @return event - 事件0-注册1-登录2-退出-1-注销
     */
    public Byte getEvent() {
        return event;
    }

    /**
     * 设置事件0-注册1-登录2-退出-1-注销
     *
     * @param event 事件0-注册1-登录2-退出-1-注销
     */
    public void setEvent(Byte event) {
        this.event = event;
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
}