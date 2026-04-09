package com.dumas.pedestal.framework.validation.annotation;

import com.dumas.pedestal.framework.validation.constraintvalidation.IPAddressValidator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * IP校验
 *
 * @author andaren
 * @version V1.0
 * @since 2020-02-26 17:23
 */
@Target({ ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = IPAddressValidator.class)
public @interface IPAddress {

    String message() default "{ipaddress.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
