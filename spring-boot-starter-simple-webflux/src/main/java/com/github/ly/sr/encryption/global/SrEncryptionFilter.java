package com.github.ly.sr.encryption.global;

import com.github.ly.enums.EncryptMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class SrEncryptionFilter implements WebFilter {
    private final Set<String> skipPaths;
    private final EncryptMode encryptMode;
    private final String privateKey;

    public SrEncryptionFilter(final Set<String> skipPaths, final EncryptMode encryptMode, final String privateKey) {
        this.skipPaths = Collections.unmodifiableSet(skipPaths);
        this.encryptMode = encryptMode;
        this.privateKey = privateKey;
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String servletPath = request.getPath().value();
        boolean gotoProcess = true;
        for (String skipPath : skipPaths) {
            if (servletPath.contains(skipPath)) {
                gotoProcess = false;
                break;
            }
        }
        if (gotoProcess) {
            HttpMethod method = request.getMethod();
            MediaType contentType = exchange.getRequest().getHeaders().getContentType();
            ServerHttpRequestDecorator requestWrapper = null;
            try {
                if (method.name().equalsIgnoreCase("options")) {
                    log.info("收到options请求");
                } else if (method.name().equalsIgnoreCase("get")) {
                    requestWrapper = new SrParameterRequestWrapper(exchange, encryptMode, privateKey);
                } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                    requestWrapper = new SrHttpRequestWrapper(exchange.getRequest(), encryptMode, privateKey);
                }
            } catch (Exception e) {
                log.error("request error", e);
            }
            if (Objects.nonNull(requestWrapper)) {
                ServerWebExchange build = exchange.mutate().request(requestWrapper).build();
                return chain.filter(build);
            }
        }
        return chain.filter(exchange);
    }
}
