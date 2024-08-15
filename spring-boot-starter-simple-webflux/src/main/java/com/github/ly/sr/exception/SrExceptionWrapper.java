package com.github.ly.sr.exception;

import com.github.ly.exception.BaseException;
import com.github.ly.model.SrResponseBody;
import com.github.ly.sr.SrProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
public class SrExceptionWrapper extends DefaultErrorWebExceptionHandler {
	private static final String[] EMPTY_ARGS = new String[0];

	@Autowired
	private SrProperties properties;

	@Autowired
	private MessageSource messageSource;

	public SrExceptionWrapper(ErrorAttributes errorAttributes, WebProperties.Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resources, errorProperties, applicationContext);
	}

	@Override
	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Throwable errorThrowable = getError(request);
		// 自定义异常默认不打印堆栈异常
		// 决定是否打印堆栈异常
		boolean printStackTrace = properties.isPrintLog();
		if (printStackTrace) {
			log.error(errorThrowable.getMessage(), errorThrowable);
		}
		SrResponseBody result;
		if (errorThrowable instanceof BaseException baseException) {
			result = baseResponseExceptionHandler(baseException);
		}
		else {
			result = SrResponseBody.failed(errorThrowable.getMessage());
		}

		return ServerResponse.status(200).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(result));
	}

	public SrResponseBody baseResponseExceptionHandler(BaseException exception) {
		int code = exception.getErrorCode();
		String message;
		if (properties.isEnableI18n()) {
			message = messageSource.getMessage(String.valueOf(code), EMPTY_ARGS, exception.getMessage(), properties.getI18nLocale());
		}
		else {
			message = exception.getMessage();
		}
		return SrResponseBody.failed(code, message, exception.getErrorData());
	}
}
