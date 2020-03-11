package com.xxc.web.interceptor;

import cn.hutool.log.StaticLog;
import com.xxc.common.consts.ConfigKey;
import com.xxc.common.enums.IpPlanEnum;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.annotation.SkipLoginCheck;
import com.xxc.service.IConfigService;
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
    private IConfigService configService;
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
        Integer ip_plan = this.configService.getIntegerValue(ConfigKey.IP_PLAN);
        IpPlanEnum planEnum = IpPlanEnum.find(ip_plan);
        switch (planEnum) {
            case WHITE_ACCESS:
                if (this.ipPlanService.isWhite(ipAddr)) {
                    break;
                }
                StaticLog.warn("非白名单IP访问阻止:IP={}", ipAddr);
                return false;
            case BLACK_DENIED:
                if (this.ipPlanService.isBlack(ipAddr)) {
                    StaticLog.warn("黑名单IP访问阻止:IP={}", ipAddr);
                    return false;
                }
                break;
            default:
                break;
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
