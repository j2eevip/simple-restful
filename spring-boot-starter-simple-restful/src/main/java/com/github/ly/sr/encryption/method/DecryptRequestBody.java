package com.github.ly.sr.encryption.method;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Slf4j
@Order(2)
@RestControllerAdvice
@RequiredArgsConstructor
public class DecryptRequestBody extends RequestBodyAdviceAdapter {
    private final String privateKey;

    @Override
    public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return parameter.hasParameterAnnotation(RequestBody.class) && parameter.hasParameterAnnotation(Decrypt.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Decrypt decrypt = parameter.getParameterAnnotation(Decrypt.class);
        if (decrypt != null) {
            log.error("Decrypt method annotated with @Decrypt is not found.");
            return inputMessage;
        }

        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                String requestBody = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
                String decryptStr = decrypt.mode().getDecryptStr(requestBody, privateKey);
                return new ByteArrayInputStream(decryptStr.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }
}
