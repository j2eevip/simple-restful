package com.github.ly.sr.encryption.global;

import com.alibaba.fastjson2.JSONObject;
import com.github.ly.constant.SrConstant;
import com.github.ly.enums.EncryptMode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

public class SrParameterRequestWrapper extends ServerHttpRequestDecorator {
    public SrParameterRequestWrapper(final ServerWebExchange exchange, final EncryptMode encryptMode, final String privateKey) {
        super(exchange.getRequest());
        from(exchange, encryptMode, privateKey);
    }

    @SuppressWarnings("unchecked")
    private void from(final ServerWebExchange exchange, final EncryptMode encryptMode, final String privateKey) {
        String decryptParam = exchange.getAttribute(SrConstant.DECRYPT_PARAM_NAME);
        if (Objects.isNull(decryptParam)) {
            return;
        }

        String requestBody = encryptMode.getDecryptStr(decryptParam, privateKey);
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        Mono<Map<String, Object>> params;
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            params = JSONObject.parseObject(requestBody, Mono.class);
        } else {
            params = WebExchangeDataBinder.extractValuesToBind(exchange);
        }
        Map<String, Object> attributes = exchange.getAttributes();
        params.map(Map::entrySet).flatMap(entries -> {
            entries.forEach(e -> attributes.put(e.getKey(), e.getValue()));
            return Mono.just(attributes);
        }).subscribe();
    }
}
