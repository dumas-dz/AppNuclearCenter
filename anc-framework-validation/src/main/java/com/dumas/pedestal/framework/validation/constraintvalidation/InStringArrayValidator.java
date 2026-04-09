package com.dumas.pedestal.framework.validation.constraintvalidation;

import com.dumas.pedestal.framework.validation.annotation.InStringArray;

import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 实现注解校验InArray
 *
 * @create 2017-07-07
 **/
public class InStringArrayValidator implements ConstraintValidator<InStringArray, Object> {

    private String[] allows;

    @Override
    public void initialize(InStringArray constraintAnnotation) {
        this.allows = constraintAnnotation.allows();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;    //非空判断由@NotBlank完成
        }
        return Stream.of(allows).anyMatch(s -> s.equals(value));
    }
}
