package com.xxc.entity.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public class UserInfo implements Serializable {
    /**
     * 用户唯一标识
     */
    private String uid;

    /**
     * 用户名称（登录）
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 用户年龄
     */
    private Integer age;

    /**
     * 用户性别 0-F 1-M
     */
    private Boolean sex;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String mailbox;

    /**
     * 地址
     */
    private String address;

    /**
     * 等级
     */
    private Byte level;

    private List<UserInfo> friendList;

    private List<GroupInfo> groupList;

    public String getUid() {
        return uid;
    }

    public UserInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserInfo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public UserInfo setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserInfo setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public UserInfo setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Boolean getSex() {
        return sex;
    }

    public UserInfo setSex(Boolean sex) {
        this.sex = sex;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public UserInfo setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getMailbox() {
        return mailbox;
    }

    public UserInfo setMailbox(String mailbox) {
        this.mailbox = mailbox;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserInfo setAddress(String address) {
        this.address = address;
        return this;
    }

    public Byte getLevel() {
        return level;
    }

    public UserInfo setLevel(Byte level) {
        this.level = level;
        return this;
    }

    public List<UserInfo> getFriendList() {
        return friendList;
    }

    public UserInfo setFriendList(List<UserInfo> friendList) {
        this.friendList = friendList;
        return this;
    }

    public List<GroupInfo> getGroupList() {
        return groupList;
    }

    public UserInfo setGroupList(List<GroupInfo> groupList) {
        this.groupList = groupList;
        return this;
    }
}
