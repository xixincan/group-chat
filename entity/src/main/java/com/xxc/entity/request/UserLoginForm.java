package com.xxc.entity.request;

import com.xxc.entity.annotation.NotEmpty;

import java.io.Serializable;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public class UserLoginForm implements Serializable {

    @NotEmpty(message = "用户名不能空")
    private String username;

    @NotEmpty(message = "密码不能空")
    private String password;

    public UserLoginForm() {
    }

    public UserLoginForm(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public UserLoginForm setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserLoginForm setPassword(String password) {
        this.password = password;
        return this;
    }
}
