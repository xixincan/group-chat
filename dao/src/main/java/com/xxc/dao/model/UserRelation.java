package com.xxc.dao.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user_relation")
public class UserRelation implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 好友
     */
    @Column(name = "fuid")
    private String fuid;

    /**
     * 有效
     */
    @Column(name = "valid")
    private Boolean valid;

    @Column(name = "updated")
    private Date updated;

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
     * 获取好友
     *
     * @return fuid - 好友
     */
    public String getFuid() {
        return fuid;
    }

    /**
     * 设置好友
     *
     * @param fuid 好友
     */
    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    /**
     * 获取有效
     *
     * @return valid - 有效
     */
    public Boolean getValid() {
        return valid;
    }

    /**
     * 设置有效
     *
     * @param valid 有效
     */
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     * @return updated
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
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