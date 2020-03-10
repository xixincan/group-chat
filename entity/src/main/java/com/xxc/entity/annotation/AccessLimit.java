package com.xxc.entity.annotation;

import java.lang.annotation.*;

/**
 * @version 1.0.0
 * @Author: xixincan
 * 2019-08-22
 */
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    /**
     * 指定second时间段内的访问次数
     *
     * @return limit
     */
    int limit() default 10;

    /**
     * 指定时间段
     *
     * @return second
     */
    int second() default 1;

}
