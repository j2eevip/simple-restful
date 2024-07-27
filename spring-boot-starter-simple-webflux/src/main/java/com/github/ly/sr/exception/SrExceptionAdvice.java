package com.github.ly.sr.exception;

import com.github.ly.exception.BaseException;
import com.github.ly.sr.SrProperties;
import com.github.ly.sr.response.SrResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Integer.MAX_VALUE - 1)
@RestControllerAdvice
public class SrExceptionAdvice {
    private static final String[] EMPTY_ARGS = new String[0];

    @Autowired
    private SrProperties properties;

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public SrResponseBody baseResponseExceptionHandler(BaseException exception) {
        if (properties.isPrintLog()) {
            log.error("==================================>\n", exception);
        }
        int code = exception.getErrorCode();
        String message;
        if (properties.isEnableI18n()) {
            message = messageSource.getMessage(String.valueOf(code), EMPTY_ARGS, exception.getMessage(), properties.getI18nLocale());
        } else {
            message = exception.getMessage();
        }
        return SrResponseBody.fail(code, message, exception.exceptionData());
    }

    @ExceptionHandler(Throwable.class)
    public SrResponseBody exceptionHandler(Throwable throwable) {
        if (properties.isPrintLog()) {
            log.error("==================================>\n", throwable);
        }
        return SrResponseBody.fail(properties.getErrorCode(), throwable.getMessage());
    }

}
