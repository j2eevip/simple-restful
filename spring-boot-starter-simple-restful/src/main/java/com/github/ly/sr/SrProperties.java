package com.github.ly.sr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = SrConstants.PROPERTIES_PREFIX)
public class SrProperties {
    private boolean printLog = false;
    private Class<?> responseClass;
    private Integer successCode = SrConstants.DEFAULT_SUCCESS_CODE;
    private String successMsg = SrConstants.DEFAULT_SUCCESS_MSG;
    private Integer errorCode = SrConstants.DEFAULT_ERROR_CODE;
    private String errorMsg = SrConstants.DEFAULT_ERROR_MSG;
    private Integer validErrorCode = SrConstants.DEFAULT_VALIDATION_ERROR_CODE;
    private String basePackage;
    private Set<String> excludePackages;
    private Set<String> excludeUrls;
    private Set<Class<?>> excludeReturnTypes;
    private String customJsonMessageConverterName;
    private boolean enableI18n = false;
    private Locale i18nLocale = LocaleContextHolder.getLocale();
}
