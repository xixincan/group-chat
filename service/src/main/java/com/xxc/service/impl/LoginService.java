package com.xxc.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.xxc.common.cache.RedisService;
import com.xxc.common.consts.ConfigKey;
import com.xxc.common.util.TicketUtil;
import com.xxc.entity.enums.UserEventEnum;
import com.xxc.entity.enums.UserStatusEnum;
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
import java.util.concurrent.CompletableFuture;

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
        String ticket = TicketUtil.getTicket(request);
        if (StrUtil.isNotEmpty(ticket)) {
            String uid = TicketUtil.getUid(ticket);
            return this.redisService.exist(this.getKey(uid));
        }
        return false;
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
        //添加redis缓存登录信息
        String userJson = JSONUtil.toJsonStr(user);
        this.redisService.setString(this.getKey(user.getUid()), userJson, 24 * 60 * 60);
        this.userService.recordUserLog(user.getUid(), request, UserEventEnum.LOGIN);
        //设置cookie
        Cookie cookie = new Cookie(ConfigKey.TICKET, TicketUtil.genTicket(user.getUid()));
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

    private String getKey(String uid) {
        return ConfigKey.USER_KEY + uid;
    }

    /**
     * 登出
     *
     * @param request request
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            //删除cookie
            if (ConfigKey.TICKET.equals(cookie.getName())) {
                final String uid = TicketUtil.getUid(cookie.getValue());
                if (StrUtil.isEmpty(uid)) {
                    return;
                }
                CompletableFuture.runAsync(() -> {
                    this.redisService.remove(this.getKey(uid));
                    this.userService.recordUserLog(uid, request, UserEventEnum.LOGOUT);
                }).thenRunAsync(() -> {
                    StaticLog.info("用户成功登出:{}", uid);
                });
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
