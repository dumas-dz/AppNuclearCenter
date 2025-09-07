package com.dumas.pedestal.framework.springboot.web.intercepter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dumas.pedestal.common.util.text.StrFormatter;
import com.dumas.pedestal.framework.springboot.constant.enums.BusinessType;
import com.dumas.pedestal.framework.springboot.util.IpUtils;
import com.dumas.pedestal.framework.springboot.util.ServletUtils;
import com.dumas.pedestal.framework.springboot.web.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * 日志拦截器
 *
 * @author andaren
 * @version V1.0
 * @since 2020-11-19 20:05
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "pedestal.log", name = "opt-enable", havingValue = "true")
@Aspect
public class LogAspect {
    @Value("${spring.application.name: unknown}")
    private String applicationName;
    @Resource
    private OperationLogHandler operationLogHandler;

    @Pointcut("@annotation(com.dumas.pedestal.framework.springboot.web.annotation.Log)")
    public void pointCut() {}

    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, null, result);
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void doAfterThrowException(JoinPoint joinPoint, Throwable e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Throwable e, Object result) {
        try {
            // 获得注解
            Log methodLog = getAnnotationLog(joinPoint);
            if (methodLog == null) {
                return;
            }
            OperationLog operationLog = OperationLog.ofNew(methodLog);
            // 应用信息
            operationLog.setApp(applicationName);

            // 填充http请求信息
            if (methodLog.logReq()) {
                fillReqestInfo(joinPoint, operationLog);
            }
            // 填充方法信息
            if (methodLog.logMethod()) {
                fillMethodInfo(joinPoint, methodLog, operationLog, result, e);
            }
            // 填充操作人信息
            if (methodLog.logOperator()) {
                fillOperatorInfo(joinPoint, operationLog);
            }
            // TODO 日志打印或者日志入库
//            BusinessLogger.getLogger().info(operationLog.printLog());
            operationLogHandler.handleOperationLog(operationLog);
        } catch (Throwable exp) {
            // 记录本地异常日志
            log.error("日志切面处理异常: msg={}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    private void fillReqestInfo(JoinPoint joinPoint, OperationLog operationLog) {
        operationLog.getRequestInfo().setReqUri(StrFormatter.truncatStr(ServletUtils.getRequest().getRequestURI(), 255));
        operationLog.getRequestInfo().setHttpMethod(ServletUtils.getRequest().getMethod());
    }

    private void fillMethodInfo(JoinPoint joinPoint, Log logAnnotation, OperationLog operationLog, Object result, Throwable e) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        String params = argsArrayToString(joinPoint.getArgs());
        operationLog.getMethodInfo().setClassName(className);
        operationLog.getMethodInfo().setMethodName(methodName);
        operationLog.getMethodInfo().setParam(StringUtils.substring(params, 0, 1000));
        if (Objects.nonNull(result)) {
            if (!BusinessType.SEARCH.equals(operationLog.getBizType())) {
                // 查询不返回查询结果
                if ("com.dumas.pedestal.common.model.proto.base.R".equals(result.getClass().getName())) {
                    operationLog.getMethodInfo().setResult(result);
                } else {
                    operationLog.getMethodInfo().setResult(StrFormatter.truncatStr(JSONObject.toJSONString(result), 800));
                }
            }
        }

        if (StringUtils.isBlank(operationLog.getMethodInfo().getParam())) {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (!paramsMap.isEmpty()) {
                operationLog.getMethodInfo().setParam(StringUtils.substring(paramsMap.toString(), 0, 800));
            }
        }
        // 是否发生异常
        if (e != null) {
            operationLog.getMethodInfo().setHadExp(true);
            operationLog.getMethodInfo().setExpMsg(StringUtils.substring(e.getMessage(), 0, 800));
        }
    }

    private void fillOperatorInfo(JoinPoint joinPoint, OperationLog operationLog) {
        operationLog.getOperatorInfo().setOperatorIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        operationLog.getOperatorInfo().setOperator("");
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (!isFilterObject(paramsArray[i])) {
                    Object paramObj = paramsArray[i];
                    if ("com.google.protobuf.GeneratedMessageV3".equals(paramObj.getClass().getSuperclass().getName())) {
                        params += paramObj.toString();
                    } else {
                        Object jsonObj = JSON.toJSON(paramsArray[i]);
                        params += jsonObj.toString() + " ";
                    }
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    private boolean isFilterObject(final Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}
