package com.github.ly.sr;

import com.github.ly.annotation.IgnoreAdvice;
import lombok.NonNull;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

public class SrGlobalWebFilter implements WebFilter {
	@Override
	@NonNull
	public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
		if (support(exchange)) {
			ServerHttpResponse originalResponse = exchange.getResponse();
			ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
				@Override
				@NonNull
				public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
					if (body instanceof Flux<? extends DataBuffer> fluxBody) {
						return super.writeWith(ResponseUtils.genFluxBody(exchange, fluxBody));
					}
					else if (body instanceof Mono<? extends DataBuffer> monoBody) {
						return super.writeWith(ResponseUtils.genMonoBody(exchange, monoBody));
					}
					return super.writeWith(body);
				}
			};
			return chain.filter(exchange.mutate().response(decoratedResponse).build());
		}
		return chain.filter(exchange);
	}

	private boolean support(ServerWebExchange exchange) {
		Object handler = exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
		if (handler instanceof HandlerMethod handlerMethod) {
			return handlerMethod.hasMethodAnnotation(IgnoreAdvice.class);
		}
		return false;
	}
}
