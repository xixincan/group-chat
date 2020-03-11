package com.xxc.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xxc.common.cache.RedisService;
import com.xxc.common.consts.ConfigKey;
import com.xxc.entity.enums.UserEventEnum;
import com.xxc.entity.enums.UserStatusEnum;
import com.xxc.common.util.ThreadLocalUtil;
import com.xxc.dao.model.User;
import com.xxc.entity.exp.ValidException;
import com.xxc.entity.request.UserLoginForm;
import com.xxc.service.ILoginService;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class LoginService implements ILoginService {

    @Resource
    private IUserService userService;
    @Resource
    private RedisService redisService;

    /**
     * 登录校验
     *
     * @param request http servlet request
     * @return login
     */
    @Override
    public boolean checkLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return null != session.getAttribute(ConfigKey.TICKET);
    }

    /**
     * 登录
     *
     * @param request       request
     * @param userLoginForm form
     */
    @Override
    public void doLogin(HttpServletRequest request, HttpServletResponse response, UserLoginForm userLoginForm) {
        User user = this.userService.getUser(userLoginForm.getUsername());
        if (null == user || !StrUtil.equals(userLoginForm.getPassword(), user.getPassword())) {
            throw new ValidException("用户名或密码错误");
        }
        if (user.getStatus() != UserStatusEnum.NORMAL.getStatus()) {
            throw new ValidException("用户状态不合法");
        }
        //添加session
        String userInfo = JSONUtil.toJsonStr(user);
        request.getSession().setAttribute(ConfigKey.TICKET, user.getUid());
        ThreadLocalUtil.bind(user.getUid(), userInfo);
        this.redisService.setString(user.getUid(), userInfo, 24 * 60 * 60);
        this.userService.recordUserLog(user.getUid(), request, UserEventEnum.LOGIN);
        //设置cookie
        Cookie cookie = new Cookie(ConfigKey.TICKET, user.getUid());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 登出
     *
     * @param request request
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute(ConfigKey.TICKET);
        if (null != uid) {
            // 注销本地session
            session.removeAttribute(ConfigKey.TICKET);
            session.invalidate();
            ThreadLocalUtil.unBind(uid);
            this.redisService.remove(uid);
        }
        //删除cookie
        Cookie cookie = new Cookie(ConfigKey.TICKET, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        this.userService.recordUserLog(uid, request, UserEventEnum.LOGOUT);
    }
}
