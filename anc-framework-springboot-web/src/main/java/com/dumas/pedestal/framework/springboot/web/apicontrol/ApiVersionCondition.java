package com.dumas.pedestal.framework.springboot.web.apicontrol;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * API 版本判断
 *
 * @author andaren
 * @version V1.0
 * @since 2020-05-12 16:46
 */
@Slf4j
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    /**
     * 从 版本 信息中提取版本 号数字的正则
     * 例如：[v0-9]
     */
    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v(\\d+)/");

    private final int apiVersion;

    public ApiVersionCondition(int apiVersion) {
        log.info("ApiVersionCondition apiVersion ==> " + apiVersion);
        log.info("ApiVersionCondition Init...");
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {

        // 采用最后定义优先原则，则方法上的定义覆盖类上面的定义
        return new ApiVersionCondition(other.apiVersion);
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getRequestURI());
        if (matcher.find()) {
            Integer version = Integer.valueOf(matcher.group(1));
            // 当应用比配置更大的版本号时，它将采取提取
            if (version >= this.apiVersion) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return other.apiVersion - this.apiVersion;
    }

    public int getApiVersion() {
        return this.apiVersion;
    }
}
