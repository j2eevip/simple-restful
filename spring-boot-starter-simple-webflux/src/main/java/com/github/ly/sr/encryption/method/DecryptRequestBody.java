package com.github.ly.sr.encryption.method;

import com.github.ly.sr.SrProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Order(0)
@RestControllerAdvice
public class DecryptRequestBody {
    private final String privateKey;

    public DecryptRequestBody(final SrProperties properties) {
        this.privateKey = properties.getPrivateKey();
    }

    public boolean supports(MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return parameter.hasParameterAnnotation(RequestBody.class) && parameter.hasParameterAnnotation(Decrypt.class);
    }

    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        Decrypt decrypt = parameter.getParameterAnnotation(Decrypt.class);
        if (Objects.isNull(decrypt)) {
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
