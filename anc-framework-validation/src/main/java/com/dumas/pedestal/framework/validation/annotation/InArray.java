package com.dumas.pedestal.framework.validation.annotation;

import com.dumas.pedestal.framework.validation.constraintvalidation.InArrayValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 实现注解校验InArray
 **/
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { InArrayValidator.class})
public @interface InArray {

    String message() default "输入值不在约定范围之内";

    int[] range() default {0, 1};//范围

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
