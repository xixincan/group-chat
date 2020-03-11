package com.xxc.web.aspect;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.google.common.base.Stopwatch;
import com.xxc.entity.result.MyResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

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

    private Stopwatch stopWatch = Stopwatch.createUnstarted();

    /**
     * 切面
     */
    @Pointcut("execution(* com.xxc.web.controller..*(..))")
    private void controller() {

    }

    @Around("controller()")
    public Object handleAround(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        StaticLog.debug("method invoke: {}", method.getName());

        Object proceed;
        this.stopWatch.start();
        try {
            proceed = joinPoint.proceed();
            this.stopWatch.stop();
            StaticLog.debug("耗时:{}ms; method return: {}", this.stopWatch.elapsed(TimeUnit.MILLISECONDS), JSONUtil.toJsonStr(proceed));
        } catch (Throwable throwable) {
            StaticLog.error("method proceed exception:{}", throwable);
            //将错误统一封装
            return MyResult.error(9999, throwable.getMessage(), throwable);
        } finally {
            this.stopWatch.reset();
        }
        return proceed;
    }

}
