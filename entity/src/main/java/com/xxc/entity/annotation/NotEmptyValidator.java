package com.xxc.entity.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 不能为空值校验
 */
public class NotEmptyValidator implements ConstraintValidator<NotEmpty, Object> {

    @Override
    public void initialize(NotEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            return values.length > 0;
        }
        return value != null && !value.toString().trim().isEmpty();
    }

}
