package com.xxc.entity.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不能为空
 */
@Documented
@Constraint(validatedBy = NotEmptyValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface NotEmpty {

    String value() default "";

    String message() default "参数不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
