package com.xxc.entity.model;

import com.xxc.entity.enums.UserEventEnum;

import java.io.Serializable;

/**
 * ZK节点模型 - 用户事件
 *
 * @author xixincan
 * 2020-03-17
 * @version 1.0.0
 */
public class ClusterChannelEvent implements Serializable {

    //机器ID
    private String sid;
    //channelID
    private String cid;
    //事件（参考 UserEventEnum）
    private Integer event;

    public ClusterChannelEvent() {

    }

    public ClusterChannelEvent(String sid, String cid, Integer event) {
        this.sid = sid;
        this.cid = cid;
        this.event = event;
    }

    public ClusterChannelEvent(String sid, String cid, UserEventEnum event) {
        this.sid = sid;
        this.cid = cid;
        this.event = (int) event.getEvent();
    }

    public String getSid() {
        return sid;
    }

    public ClusterChannelEvent setSid(String sid) {
        this.sid = sid;
        return this;
    }

    public String getCid() {
        return cid;
    }

    public ClusterChannelEvent setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public Integer getEvent() {
        return event;
    }

    public ClusterChannelEvent setEvent(Integer event) {
        this.event = event;
        return this;
    }
}
