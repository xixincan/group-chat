package com.xxc.web.aspect;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 拦截器切面
 *
 * @version 1.0.0
 * @author: xixincan
 * @date: 2019-05-25
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebAspect {

    /**
     * 切面
     */
    @Pointcut("execution(* com.xxc.web.controller..*(..))")
    private void controller() {

    }

    @Around("controller()")
    public Object handleAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        StaticLog.info("&^^%&*^ invoke {}", method.getName());
        Object proceed = joinPoint.proceed();
        StaticLog.info("^^^^^&&&&{}", JSONUtil.toJsonStr(proceed));
        return proceed;
    }

}
