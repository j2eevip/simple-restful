package com.github.ly.sr.encryption.method;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.github.ly.sr.SrConstant;
import com.github.ly.sr.exception.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class DecryptGetParam implements HandlerInterceptor {

    private final String privateKey;

    public DecryptGetParam(final String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if ("GET".equals(request.getMethod()) && (handler instanceof HandlerMethod method) && (method.hasMethodAnnotation(Decrypt.class))) {
                String decryptBody = request.getParameter(SrConstant.DECRYPT_PARAM_NAME);
                Decrypt decrypt = method.getMethodAnnotation(Decrypt.class);
                String requestBody = Objects.nonNull(decrypt) ? decrypt.mode().getDecryptStr(decryptBody, privateKey) : "";
                if (StringUtils.hasText(requestBody) && !SrConstant.EMPTY_JSON_BODY.equals(requestBody)) {
                    Map<String, Object> requestMap = JSONObject.parseObject(requestBody, new TypeReference<>() {
                    });
                    for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.error("异常地址：{}", request.getServletPath(), e);
            ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "Decrypt failed", e);
        }
        return true;
    }
}
