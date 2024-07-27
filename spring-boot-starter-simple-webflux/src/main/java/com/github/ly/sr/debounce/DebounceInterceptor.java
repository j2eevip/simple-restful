package com.github.ly.sr.debounce;

import com.github.ly.sr.exception.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Objects;

@ConditionalOnBean(CacheSupport.class)
@RequiredArgsConstructor
public class DebounceInterceptor {
    private final CacheSupport cacheSupport;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ((handler instanceof HandlerMethod handlerMethod) && handlerMethod.hasMethodAnnotation(Debounce.class)) {
            String cacheKey = cacheKey(request, handlerMethod.getMethod());
            if (Boolean.TRUE.equals(cacheSupport.hasCacheKey(cacheKey))) {
                ExceptionUtil.raiseException("bad request: duplicate submit, please wait some moments do it again.");
            }
            Debounce debounce = handlerMethod.getMethodAnnotation(Debounce.class);
            assert debounce != null;
            cacheSupport.setCacheKey(cacheKey, "1", debounce.expire(), debounce.unit());
        }
        return true;
    }


    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String cacheKey = cacheKey(request, ((HandlerMethod) handler).getMethod());
        cacheSupport.removeCacheKey(cacheKey);
        if (Objects.nonNull(ex)) {
            ExceptionUtil.raiseException(500, "request exception", ex);
        }
    }

    private String cacheKey(HttpServletRequest request, Method method) {
        String sessionId = request.getSession(false).getId();
        return "debounce:" + method.getName() + ":" + sessionId;
    }
}
