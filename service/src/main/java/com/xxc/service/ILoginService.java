package com.xxc.service;

import com.xxc.entity.request.UserLoginForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
public interface ILoginService {

    /**
     * 登录校验
     *
     * @param request http servlet request
     * @return login
     */
    boolean checkLogin(HttpServletRequest request);

    /**
     * 登录
     *
     * @param request request
     * @param userLoginForm form
     */
    void doLogin(HttpServletRequest request, HttpServletResponse response, UserLoginForm userLoginForm);

    /**
     * 登出
     *
     * @param request request
     */
    void logout(HttpServletRequest request, HttpServletResponse response);
}
