package com.xxc.entity.request;

import com.xxc.entity.annotation.NotEmpty;

import java.io.Serializable;

/**
 * Created by xixincan
 * 2020-03-19
 *
 * @version 1.0.0
 */
public class UserRegisterForm implements Serializable {

    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "昵称不能为空")
    private String nickname;
    @NotEmpty(message = "邮箱不能为空")
    private String mailbox;
    private String mobile;
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMailbox() {
        return mailbox;
    }

    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
