package com.github.ly.sr.exception;

import com.github.ly.sr.SrProperties;
import com.github.ly.sr.response.SrResponseBody;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Order(Integer.MAX_VALUE - 4)
@RestControllerAdvice
public class SrExceptionAdvice {
    private static final String[] EMPTY_ARGS = new String[0];

    @Resource
    private SrProperties properties;

    @Resource
    private MessageSource messageSource;

    @ExceptionHandler(BaseResponseException.class)
    public SrResponseBody baseResponseExceptionHandler(BaseResponseException exception) {
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

    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public SrResponseBody validationExceptionHandler(Exception exception) throws Exception {
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            HttpStatusCode statusCode = methodArgumentNotValidException.getStatusCode();
            String msg = mergeErrorMessage(methodArgumentNotValidException.getBindingResult().getAllErrors());
            return SrResponseBody.fail(statusCode.value(), msg);
        } else if (exception instanceof BindException bindException) {
            String msg = mergeErrorMessage(bindException.getBindingResult().getAllErrors());
            return SrResponseBody.fail(properties.getValidErrorCode(), msg);
        } else if (exception instanceof ConstraintViolationException constraintViolationException) {
            Set<ConstraintViolation<?>> violationSet = constraintViolationException.getConstraintViolations();
            String msg = violationSet.stream().map(s -> s.getConstraintDescriptor().getMessageTemplate()).collect(Collectors.joining(";"));
            return SrResponseBody.fail(properties.getValidErrorCode(), msg);
        }

        return SrResponseBody.fail(properties.getErrorCode(), exception.getMessage());
    }

    private String mergeErrorMessage(List<ObjectError> allErrors) {
        if (CollectionUtils.isEmpty(allErrors)) {
            return "";
        }
        return allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
    }

    @ExceptionHandler(Throwable.class)
    public SrResponseBody exceptionHandler(Throwable throwable) {
        if (properties.isPrintLog()) {
            log.error("==================================>\n", throwable);
        }
        return SrResponseBody.fail(properties.getErrorCode(), throwable.getMessage());
    }

}
