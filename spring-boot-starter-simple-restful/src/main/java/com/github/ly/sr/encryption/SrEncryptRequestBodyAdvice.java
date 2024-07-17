package com.github.ly.sr.encryption;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Objects;

public class SrEncryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    private final boolean isGlobalEncryption;

    public SrEncryptRequestBodyAdvice(final boolean isGlobalEncryption) {
        this.isGlobalEncryption = isGlobalEncryption;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method;
        return this.isGlobalEncryption ||
                (Objects.nonNull(method = methodParameter.getMethod())
                        && method.isAnnotationPresent(DecryptRequestBody.class)
                        && targetType.getClass().isAnnotationPresent(RestController.class));
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
//        parameter.getExecutable().

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return null;
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }
}
