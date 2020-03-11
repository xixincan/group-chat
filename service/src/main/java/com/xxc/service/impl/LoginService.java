package com.xxc.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xxc.common.cache.RedisService;
import com.xxc.common.enums.UserEventEnum;
import com.xxc.common.enums.UserStatusEnum;
import com.xxc.common.util.ThreadLocalUtil;
import com.xxc.dao.model.User;
import com.xxc.entity.exp.ValidException;
import com.xxc.entity.request.UserLoginForm;
import com.xxc.service.IConfigService;
import com.xxc.service.ILoginService;
import com.xxc.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author xixincan
 * 2020-03-11
 * @version 1.0.0
 */
@Service
public class LoginService implements ILoginService {

    private static final String TICKET = "ticket";

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
        return null != session.getAttribute(TICKET);
    }

    /**
     * 登录
     *
     * @param request       request
     * @param userLoginForm form
     */
    @Override
    public void doLogin(HttpServletRequest request, UserLoginForm userLoginForm) {
        User user = this.userService.getUser(userLoginForm.getUsername());
        if (null == user || !StrUtil.equals(userLoginForm.getPassword(), user.getPassword())) {
            throw new ValidException("用户名或密码错误");
        }
        if (user.getStatus() != UserStatusEnum.NORMAL.getStatus()) {
            throw new ValidException("用户状态不合法");
        }
        String userInfo = JSONUtil.toJsonStr(user);
        request.getSession().setAttribute(TICKET, user.getUid());
        ThreadLocalUtil.bind(user.getUid(), userInfo);
        this.redisService.setString(user.getUid(), userInfo, 24 * 60 * 60);
        this.userService.userLog(user.getUid(), request, UserEventEnum.LOGIN);
    }

    /**
     * 登出
     *
     * @param request request
     */
    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String uid = (String) session.getAttribute(TICKET);
        if (null != uid) {
            // 注销本地session
            session.removeAttribute(TICKET);
            session.invalidate();
            ThreadLocalUtil.unBind(uid);
            this.redisService.remove(uid);
        }
        this.userService.userLog(uid, request, UserEventEnum.LOGOUT);
    }
}
