package com.github.ly.sr;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "simple-restful")
public class SrProperties {
    private boolean printLog = false;
    private Integer successCode = 200;
    private String successMsg = "success";
    private Integer errorCode = 500;
    private String errorMsg = "request fail.";
    private Integer validErrorCode = 502;
    private boolean enableI18n = false;
    private Locale i18nLocale = LocaleContextHolder.getLocale();

    private String basePackage;
    private Set<String> excludePackages;
    private Set<String> excludeUrls;
    private Set<Class<?>> excludeReturnTypes;
    private String customJsonMessageConverterName;
}
