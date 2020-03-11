package com.xxc.service;

import com.xxc.common.enums.UserEventEnum;
import com.xxc.dao.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
public interface IUserService {

    /**
     * user
     *
     * @param username username
     * @return user
     */
    User getUser(String username);

    /**
     * 记录
     *
     * @param uid     uid
     * @param request req
     * @param event   ev
     */
    void userLog(String uid, HttpServletRequest request, UserEventEnum event);
}
