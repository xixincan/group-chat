package com.xxc.web.interceptor;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.google.common.util.concurrent.RateLimiter;
import com.xxc.entity.annotation.AccessLimit;
import com.xxc.common.cache.RedisService;
import com.xxc.common.util.MyIPUtil;
import com.xxc.entity.result.MyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @version 1.0.0
 * @Author: xixincan
 * 2019-08-22
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    private String ACCESS = "access:";

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(10d);

    @Autowired
    private RedisService redisService;

    /**
     * Intercept the execution of a handler. Called after HandlerMapping determined
     * an appropriate handler object, but before HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending a HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(AccessLimit.class)) {
                AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
                if (null != accessLimit) {
                    int limit = accessLimit.limit();
                    int second = accessLimit.second();
                    String ipAddr = MyIPUtil.getRemoteIpAddr(request);
                    String key = ACCESS + ipAddr + request.getRequestURI();
                    Integer currentValue = this.redisService.getInteger(key);
                    if (null == currentValue) {
                        this.redisService.setInteger(key, 1, second);
                    } else {
                        if (currentValue >= limit) {
                            StaticLog.warn("{}请求太频繁!", key);
                            this.response(response, "请求过于频繁！");
                            return false;
                        }
                        if (!RATE_LIMITER.tryAcquire()) {
                            String msg = "服务器繁忙，请稍后再试!";
                            StaticLog.warn(msg);
                            this.response(response, msg);
                            return false;
                        }
                        this.redisService.setInteger(key, currentValue + 1, second);
                    }
                }
            }
        }
//        StaticLog.info("com.xxc.web.interceptor.AccessLimitInterceptor.preHandle executed...{}", request.getRequestURL());
        return true;
    }

    private void response(HttpServletResponse response, String msg) {
        MyResult<String> myResult = new MyResult<>(200, msg);
        response.setContentType("application/json;charset=UTF-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(JSONUtil.toJsonStr(myResult).getBytes());
            outputStream.flush();
        } catch (IOException ioe) {
            StaticLog.error(ioe);
        }
    }

    /**
     * Callback after completion of request processing, that is, after rendering
     * the view. Will be called on any outcome of handler execution, thus allows
     * for proper resource cleanup.
     * <p>Note: Will only be called if this interceptor's {@code preHandle}
     * method has successfully completed and returned {@code true}!
     * <p>As with the {@code postHandle} method, the method will be invoked on each
     * interceptor in the chain in reverse order, so the first interceptor will be
     * the last to be invoked.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation is empty.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  handler (or {@link HandlerMethod}) that started asynchronous
     *                 execution, for type and/or instance examination
     * @param ex       exception thrown on handler execution, if any
     * @throws Exception in case of errors
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        StaticLog.info("^^^^^&&&&{}", "------- AccessLimitInterceptor - afterCompletion --------");
    }
}
