package com.xxc.entity.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sanshou on 15/4/20.
 */

@Documented
@Constraint(validatedBy = LengthMinValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface LengthMin {

    String message() default "参数不合法";

    int value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
