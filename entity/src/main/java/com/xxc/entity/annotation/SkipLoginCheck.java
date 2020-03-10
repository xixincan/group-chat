package com.xxc.entity.annotation;

import java.lang.annotation.*;

/**
 * 日志
 * Created by qiya on 15/9/20.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipLoginCheck {
}
