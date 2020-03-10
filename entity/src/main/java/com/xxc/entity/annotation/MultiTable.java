package com.xxc.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义分表注解
 *
 * @author xixincan
 * 2020-03-10
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiTable {

    /**
     * 分表个数
     *
     * @return total table number
     */
    int value() default 1;

}
