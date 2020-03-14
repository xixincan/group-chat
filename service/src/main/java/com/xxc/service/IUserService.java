package com.xxc.service;

import com.xxc.entity.enums.UserEventEnum;
import com.xxc.dao.model.User;
import com.xxc.entity.response.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

/**
 * @Author: xixincan
 * @Date: 2019/5/20
 * @Version 1.0
 */
public interface IUserService {

    /**
     * 获取用户对象
     */
    User getUser(String username);

    /**
     * 获取用户信息（获取用户自身的）
     */
    UserInfo getSelfUserInfo(HttpServletRequest request);

    /**
     * 获取用户简要信息
     */
    UserInfo getUserSimpleInfo(String uid);

    /**
     * 获取用户简要信息（获取其他用户的）
     */
    List<UserInfo> getUserSimpleInfoList(Collection<String> uidList);

    /**
     * 记录
     */
    void recordUserLog(String uid, HttpServletRequest request, UserEventEnum event);
}
