package com.github.ly.sr.encryption.method;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.ly.annotation.Decrypt;
import com.github.ly.constant.SrConstant;
import com.github.ly.tools.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class DecryptGetParam {

    private final String privateKey;

    public DecryptGetParam(final String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean preHandle(ServerWebExchange exchange, Object handler) throws Exception {
        String decryptBody;
        ServerHttpRequest request = exchange.getRequest();
        if ("GET".equals(request.getMethod().name())
                && (handler instanceof HandlerMethod method)
                && method.hasMethodAnnotation(Decrypt.class)
                && StringUtils.hasText(decryptBody = exchange.getAttribute(SrConstant.DECRYPT_PARAM_NAME))) {
            Decrypt decrypt = method.getMethodAnnotation(Decrypt.class);
            try {
                String requestBody = Objects.nonNull(decrypt) ? decrypt.mode().getDecryptStr(decryptBody, privateKey) : "";
                if (StringUtils.hasText(requestBody) && !SrConstant.EMPTY_JSON_BODY.equals(requestBody)) {
                    JSONObject requestMap = JSON.parseObject(requestBody);
                    Map<String, Object> attributes = exchange.getAttributes();
                    attributes.putAll(requestMap);
                }
            } catch (Exception e) {
                log.error("异常地址：{}", request.getPath().value(), e);
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "Decrypt failed", e);
            }
        }
        return true;
    }
}
