package com.github.ly.sr.debounce;

import com.github.ly.annotation.Debounce;
import com.github.ly.cache.CacheSupport;
import com.github.ly.tools.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

@ConditionalOnBean(CacheSupport.class)
@RequiredArgsConstructor
public class DebounceInterceptor {
    private final CacheSupport cacheSupport;

    public boolean preHandle(ServerWebExchange exchange, Object handler) throws Exception {
        if ((handler instanceof HandlerMethod handlerMethod) && handlerMethod.hasMethodAnnotation(Debounce.class)) {
            String cacheKey = cacheKey(exchange, handlerMethod.getMethod()).block();
            if (Boolean.TRUE.equals(cacheSupport.hasCacheKey(cacheKey))) {
                ExceptionUtil.raiseException("bad request: duplicate submit, please wait some moments do it again.");
            }
            Debounce debounce = handlerMethod.getMethodAnnotation(Debounce.class);
            assert debounce != null;
            cacheSupport.setCacheKey(cacheKey, "1", debounce.expire(), debounce.unit());
        }
        return true;
    }


    private Mono<String> cacheKey(ServerWebExchange exchange, Method method) {
        return exchange.getSession().map(s -> "debounce:" + method.getName() + ":" + s.getId());
    }
}
