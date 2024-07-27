package com.github.ly.sr.encryption.method;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.ly.sr.SrConstant;
import com.github.ly.sr.exception.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class DecryptGetParam {

    private final String privateKey;

    public DecryptGetParam(final String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String decryptBody;

        if ("GET".equals(request.getMethod())
                && (handler instanceof HandlerMethod method)
                && method.hasMethodAnnotation(Decrypt.class)
                && StringUtils.hasText(decryptBody = request.getParameter(SrConstant.DECRYPT_PARAM_NAME))) {
            Decrypt decrypt = method.getMethodAnnotation(Decrypt.class);
            try {
                String requestBody = Objects.nonNull(decrypt) ? decrypt.mode().getDecryptStr(decryptBody, privateKey) : "";
                if (StringUtils.hasText(requestBody) && !SrConstant.EMPTY_JSON_BODY.equals(requestBody)) {
                    JSONObject requestMap = JSON.parseObject(requestBody);
                    for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.error("异常地址：{}", request.getServletPath(), e);
                ExceptionUtil.raiseException(SrConstant.DEFAULT_FAIL_CODE, "Decrypt failed", e);
            }
        }
        return true;
    }
}
