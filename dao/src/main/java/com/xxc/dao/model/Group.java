package com.xxc.dao.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "group")
public class Group implements Serializable {
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 群名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 群头像
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 群主
     */
    @Column(name = "owner")
    private String owner;

    /**
     * 群状态-1废弃0禁用1正常
     */
    @Column(name = "status")
    private Byte status;

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
     * 获取群名称
     *
     * @return name - 群名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置群名称
     *
     * @param name 群名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取群头像
     *
     * @return avatar - 群头像
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置群头像
     *
     * @param avatar 群头像
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取群主
     *
     * @return owner - 群主
     */
    public String getOwner() {
        return owner;
    }

    /**
     * 设置群主
     *
     * @param owner 群主
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 获取群状态-1废弃0禁用1正常
     *
     * @return status - 群状态-1废弃0禁用1正常
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置群状态-1废弃0禁用1正常
     *
     * @param status 群状态-1废弃0禁用1正常
     */
    public void setStatus(Byte status) {
        this.status = status;
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