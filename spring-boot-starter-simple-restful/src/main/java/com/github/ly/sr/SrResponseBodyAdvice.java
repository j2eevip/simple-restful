package com.github.ly.sr;

import com.alibaba.fastjson.JSON;
import com.github.ly.sr.annotation.IgnoreAdvice;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestControllerAdvice
@Order(value = Integer.MAX_VALUE - 3)
public class SrResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Resource
    private SrProperties properties;

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> clazz) {
        Method method = methodParameter.getMethod();

        if (Objects.isNull(method)
                || method.isAnnotationPresent(IgnoreAdvice.class)
                || method.getReturnType().equals(SrResponseBody.class)
                || AbstractJsonHttpMessageConverter.class.isAssignableFrom(clazz)
                || AbstractJackson2HttpMessageConverter.class.isAssignableFrom(clazz)) {
            log.debug("Method为空、返回值为void和Response类型、非JSON或者使用注解 IgnoreAdvice,跳过方法。");
            return false;
        }

        String customJsonHttpMessageConvertName;
        if (StringUtils.hasText(customJsonHttpMessageConvertName = properties.getCustomJsonMessageConverterName())
                && !clazz.getName().equals(customJsonHttpMessageConvertName)) {
            log.debug("自定义的JsonConvert和方法使用的convert不同，跳过方法");
            return false;
        }

        Set<Class<?>> excludeReturnTypes = properties.getExcludeReturnTypes();
        if (!CollectionUtils.isEmpty(excludeReturnTypes)
                && excludeReturnTypes.contains(method.getReturnType())) {
            log.debug("匹配到excludeReturnTypes例外配置，跳过:returnType={},", method.getReturnType());
            return false;
        }

        Set<String> excludePackages = properties.getExcludePackages();
        if (!CollectionUtils.isEmpty(excludePackages)) {
            String packageName = method.getDeclaringClass().getPackage().getName();
            if (excludePackages.stream().anyMatch(item -> ANT_PATH_MATCHER.match(item, packageName))) {
                log.debug("匹配到excludePackages例外配置，跳过:packageName={},", packageName);
                return false;
            }
        }

        Set<String> excludeUrls = properties.getExcludeUrls();
        if (!CollectionUtils.isEmpty(excludeUrls)) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String requestURI = request.getRequestURI();
            for (String excludeUrl : excludeUrls) {
                if (ANT_PATH_MATCHER.match(excludeUrl, requestURI)) {
                    log.debug("匹配到excludeUrls例外配置，跳过:excludeUrl={},requestURI={}",
                            excludeUrl, requestURI);
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> clazz,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        Method method = methodParameter.getMethod();
        if ((Objects.nonNull(method) && method.getReturnType().equals(Void.TYPE)) || Objects.isNull(body)) {
            return SrResponseBody.success(properties.getSuccessCode());
        } else if (body instanceof SrResponseBody) {
            return body;
        } else if (body instanceof String) {
            return JSON.toJSONString(SrResponseBody.success(properties.getSuccessCode(), body));
        }
        if (properties.isPrintLog()) {
            String path = serverHttpRequest.getURI().getPath();
            log.debug("非空返回值，执行封装:path={}", path);
        }
        return SrResponseBody.success(properties.getSuccessCode(), body);
    }
}
