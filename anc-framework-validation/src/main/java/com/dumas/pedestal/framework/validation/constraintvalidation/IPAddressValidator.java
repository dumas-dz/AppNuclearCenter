package com.dumas.pedestal.framework.validation.constraintvalidation;

import com.dumas.pedestal.framework.validation.annotation.IPAddress;

import static java.util.regex.Pattern.compile;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * IP字符串合法性校验
 *
 * @author andaren
 * @version V1.0
 * @since 2020-02-26 18:16
 */
public class IPAddressValidator implements ConstraintValidator<IPAddress, String> {
    // 完整正则："^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])\\.((([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))))\\.((([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))))\\.((([0-9]|([1-9]\\d)|(1\\d\\d)|(2([0-4]\\d|5[0-5])))))$"
    private static final Pattern IP_PATTERN = compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            // 非空判断由@NotNull完成
            return true;
        }
        Matcher matcher = IP_PATTERN.matcher(value);
        try {
            if (!matcher.matches()) {
                return false;
            } else {
                for (int i = 1; i <= 4; i++) {
                    int octet = Integer.valueOf(matcher.group(i));
                    if (octet > 255) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
