package com.dumas.pedestal.framework.validation.annotation;

import com.dumas.pedestal.framework.validation.constraintvalidation.InStringArrayValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 实现注解校验InStringArray
 **/
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { InStringArrayValidator.class })
public @interface InStringArray {

    String message() default "输入值不在约定范围之内";

    /**
     * 范围, 保证数组已排序
     * @return
     */
    String[] allows() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
