package com.xxc.web.interceptor;

import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.service.IIpPlanService;
import com.xxc.service.ILoginService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private IIpPlanService ipPlanService;
    @Resource
    private ILoginService loginService;

    /**
     * 登录校验，没有通过的直接跳转至登录页面
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            //RPC访问
            return true;
        }
        //获取访问的真实ip
        String ipAddr = MyIPUtil.getRemoteIpAddr(request);
        if (!this.ipPlanService.checkIpAddr(ipAddr)) {
            response.sendRedirect("/error/401.html");
            return false;
        }

        // 如果请求登录页
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        SkipLoginCheck skipLoginCheck = handlerMethod.getMethodAnnotation(SkipLoginCheck.class);
        if (null != skipLoginCheck) {
            //免登录权限
            return true;
        }

        //登录校验
        if (this.loginService.checkLogin(request)) {
            return true;
        }

        StringBuffer requestURL = request.getRequestURL();
        StringBuffer loginURI = requestURL
                .delete(requestURL.length() - request.getRequestURI().length(), requestURL.length())
                .append("/login.html");
        response.sendRedirect(loginURI.toString());
        return false;
    }
}
