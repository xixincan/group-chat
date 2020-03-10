package com.xxc.dao.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "user")
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户唯一标识
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 用户名称（登录）
     */
    @Column(name = "username")
    private String username;

    /**
     * 用户密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 用户昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 头像地址
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 用户年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 手机
     */
    @Column(name = "mobile")
    private String mobile;

    /**
     * 邮箱
     */
    @Column(name = "mailbox")
    private String mailbox;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 等级
     */
    @Column(name = "level")
    private Byte level;

    /**
     * 用户状态-1废弃0-冻结1-正常
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
     * 获取用户唯一标识
     *
     * @return uid - 用户唯一标识
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置用户唯一标识
     *
     * @param uid 用户唯一标识
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取用户名称（登录）
     *
     * @return username - 用户名称（登录）
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名称（登录）
     *
     * @param username 用户名称（登录）
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取用户密码
     *
     * @return password - 用户密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置用户密码
     *
     * @param password 用户密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户昵称
     *
     * @return nickname - 用户昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取头像地址
     *
     * @return avatar - 头像地址
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像地址
     *
     * @param avatar 头像地址
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取用户年龄
     *
     * @return age - 用户年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置用户年龄
     *
     * @param age 用户年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取手机
     *
     * @return mobile - 手机
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机
     *
     * @param mobile 手机
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取邮箱
     *
     * @return mailbox - 邮箱
     */
    public String getMailbox() {
        return mailbox;
    }

    /**
     * 设置邮箱
     *
     * @param mailbox 邮箱
     */
    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    /**
     * 获取地址
     *
     * @return address - 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取等级
     *
     * @return level - 等级
     */
    public Byte getLevel() {
        return level;
    }

    /**
     * 设置等级
     *
     * @param level 等级
     */
    public void setLevel(Byte level) {
        this.level = level;
    }

    /**
     * 获取用户状态-1废弃0-冻结1-正常
     *
     * @return status - 用户状态-1废弃0-冻结1-正常
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置用户状态-1废弃0-冻结1-正常
     *
     * @param status 用户状态-1废弃0-冻结1-正常
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