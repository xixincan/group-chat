package com.xxc.dao.model;

import com.xxc.dao.base.DynamicTableNameBean;
import com.xxc.entity.annotation.MultiTable;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user_msg")
public class UserMsg extends DynamicTableNameBean implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * uid
     */
    @Column(name = "uid")
    @MultiTable(4)
    private String uid;

    /**
     * msg id
     */
    @Column(name = "mid")
    private String mid;

    /**
     * 是否已发送
     */
    @Column(name = "sent")
    private Boolean sent;

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
     * 获取uid
     *
     * @return uid - uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置uid
     *
     * @param uid uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取msg id
     *
     * @return mid - msg id
     */
    public String getMid() {
        return mid;
    }

    /**
     * 设置msg id
     *
     * @param mid msg id
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    /**
     * 获取是否已发送
     *
     * @return sent - 是否已发送
     */
    public Boolean getSent() {
        return sent;
    }

    /**
     * 设置是否已发送
     *
     * @param sent 是否已发送
     */
    public void setSent(Boolean sent) {
        this.sent = sent;
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