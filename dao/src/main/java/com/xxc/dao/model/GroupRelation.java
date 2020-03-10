package com.xxc.dao.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "group_relation")
public class GroupRelation implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 群标识
     */
    @Column(name = "gid")
    private Integer gid;

    /**
     * 成员标识
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 关系0-解除1-建立
     */
    @Column(name = "valid")
    private Boolean valid;

    /**
     * 离群时间
     */
    @Column(name = "updated")
    private Date updated;

    /**
     * 进群时间
     */
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
     * 获取成员标识
     *
     * @return uid - 成员标识
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置成员标识
     *
     * @param uid 成员标识
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取关系0-解除1-建立
     *
     * @return valid - 关系0-解除1-建立
     */
    public Boolean getValid() {
        return valid;
    }

    /**
     * 设置关系0-解除1-建立
     *
     * @param valid 关系0-解除1-建立
     */
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     * 获取离群时间
     *
     * @return updated - 离群时间
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * 设置离群时间
     *
     * @param updated 离群时间
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    /**
     * 获取进群时间
     *
     * @return created - 进群时间
     */
    public Date getCreated() {
        return created;
    }

    /**
     * 设置进群时间
     *
     * @param created 进群时间
     */
    public void setCreated(Date created) {
        this.created = created;
    }
}