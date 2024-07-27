package com.github.ly.sr.encryption.global;

import com.github.ly.enums.EncryptMode;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SrHttpRequestWrapper extends HttpServletRequestWrapper {
    private String requestBody = null;

    public SrHttpRequestWrapper(final HttpServletRequest request, final EncryptMode encryptMode, final String privateKey) throws IOException {
        super(request);
        requestBody = readBody(request, encryptMode, privateKey);
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return bais.read();
            }
        };
    }

    private static String readBody(final ServletRequest request, final EncryptMode encryptMode, final String privateKey) throws IOException {
        String param = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(param)) {
            return param;
        }
        return encryptMode.getDecryptStr(param, privateKey);
    }
}
