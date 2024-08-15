package com.github.ly.sr.response;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import com.github.ly.annotation.IgnoreAdvice;
import com.github.ly.model.SrResponseBody;
import com.github.ly.sr.SrProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
public class SrResponseBodyWrapper extends ResponseBodyResultHandler {
	private static final SrResponseBody EMPTY_RESPONSE_BODY = SrResponseBody.success();

	private final SrProperties properties;

	private final String basePackageName;

	public SrResponseBodyWrapper(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver, SrProperties properties) {
		super(writers, resolver);

		this.properties = properties;
		this.basePackageName = properties.getBasePackage();
	}

	public boolean supports(HandlerResult handlerResult) {
		Method method = handlerResult.getReturnTypeSource().getMethod();
		Class<?> resolve = handlerResult.getReturnType().resolve();
		Class<?> resultType = handlerResult.getReturnType().resolveGeneric(0);
		if (Objects.isNull(method)
				|| method.isAnnotationPresent(IgnoreAdvice.class)
				|| (resolve != Mono.class && resolve != Flux.class)
				|| resultType == SrResponseBody.class) {
			log.debug("Method为空、返回值为void和Response类型、非JSON或者使用注解 IgnoreAdvice,跳过方法。");
			return false;
		}

		String packageName = method.getDeclaringClass().getPackage().getName();
		return !StringUtils.hasText(basePackageName) || packageName.startsWith(basePackageName);
	}

	@Override
	@NonNull
	public Mono<Void> handleResult(@NonNull ServerWebExchange exchange, @NonNull HandlerResult result) {
		if (properties.isPrintLog()) {
			log.debug("非空返回值，执行封装:path={}", exchange.getRequest().getPath().value());
		}
		MethodParameter bodyTypeParameter = result.getReturnTypeSource();
		Method method = bodyTypeParameter.getMethod();
		Object responseBody = result.getReturnValue();
		Mono<SrResponseBody> body;
		if ((Objects.nonNull(method) && method.getReturnType().equals(Void.TYPE)) || Objects.isNull(responseBody)) {
			body = Mono.just(EMPTY_RESPONSE_BODY);
		}
		else if (responseBody instanceof Mono<?> monoBody) {
			body = monoBody.map(this::wrapUnifyResult).defaultIfEmpty(EMPTY_RESPONSE_BODY);
		}
		else if (responseBody instanceof Flux<?> fluxBody) {
			body = fluxBody.collectList().map(this::wrapUnifyResult).defaultIfEmpty(EMPTY_RESPONSE_BODY);
		}
		else {
			body = Mono.just(wrapUnifyResult(responseBody));
		}
		return writeBody(body, bodyTypeParameter, exchange);
	}

	private SrResponseBody wrapUnifyResult(Object body) {
		if (body instanceof SrResponseBody result) {
			return result;
		}
		return SrResponseBody.success(body);
	}
}
