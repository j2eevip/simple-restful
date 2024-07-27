package com.github.ly.sr;

import com.github.ly.sr.encryption.method.EncryptMode;
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
    private boolean isGlobalEncryption = false;
    private Integer successCode = SrConstant.DEFAULT_SUCCESS_CODE;
    private String successMsg = "request was successful.";
    private Integer errorCode = SrConstant.DEFAULT_FAIL_CODE;
    private String errorMsg = SrConstant.DEFAULT_FAIL_MSG;
    private Integer validErrorCode = 502;
    private boolean enableI18n = false;
    private Locale i18nLocale = LocaleContextHolder.getLocale();

    private boolean requestBodyEncryption = false;
    private boolean globalEncryption = false;
    private Set<String> skipPaths;
    private EncryptMode encryptMode;
    private String privateKey;
    private String basePackage;
    private Set<String> excludePackages;
    private Set<String> excludeUrls;
    private Set<Class<?>> excludeReturnTypes;
    private String customJsonMessageConverterName;
}
