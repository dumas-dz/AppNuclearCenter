package com.dumas.pedestal.framework.springboot.web.intercepter;

import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误页面处理
 *
 * @author az.ye
 * @version V1.0
 * @since 2021-05-28 14:15
 */
@Controller
@ImportAutoConfiguration(classes = {ErrorProperties.class})
@Slf4j
public class ErrorPageHandler extends BasicErrorController {
    public ErrorPageHandler(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @SneakyThrows
    @RequestMapping(
        produces = {"text/html"}
    )
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections
            .unmodifiableMap(this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());

        JSONObject respJson = new JSONObject();
        respJson.put("code", String.valueOf(status.value()));
        respJson.put("msg", "resource not found or not allowed: "+ model.get("path"));

        response.getWriter().write(JSONObject.toJSONString(respJson));
        response.getWriter().flush();
        return null;
    }

    @RequestMapping
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        log.error(">>> error page.");
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            return new ResponseEntity(getErrorResponseJson(request), status);
        }
    }

    private Map<String, Object> getErrorResponseJson(HttpServletRequest request) {
        Map<String, Object> bodyMap = new HashMap<>();

        Map<String, Object> body = this.getErrorAttributes(request, this.getErrorAttributeOptions(request, MediaType.ALL));
        bodyMap.put("code", String.valueOf(body.get("status")));
        bodyMap.put("msg", "resource not found or not allowed: " + body.get("path"));

        return bodyMap;
    }


}

