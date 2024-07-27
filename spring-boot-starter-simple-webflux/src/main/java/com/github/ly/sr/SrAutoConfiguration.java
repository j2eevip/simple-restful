package com.github.ly.sr;

import com.github.ly.sr.encryption.global.SrEncryptionFilter;
import com.github.ly.sr.encryption.method.DecryptRequestBody;
import com.github.ly.sr.exception.SrExceptionAdvice;
import com.github.ly.sr.response.SrResponseBodyAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Lazy(false)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SrProperties.class)
public class SrAutoConfiguration implements InitializingBean {
    @Bean
    public SrExceptionAdvice exceptionAdvice() {
        return new SrExceptionAdvice();
    }

    @Bean
    public SrResponseBodyAdvice responseBodyAdvice(SrProperties properties) {
        return new SrResponseBodyAdvice(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "simple-restful", name = "request-body-encryption", havingValue = "true")
    public DecryptRequestBody decryptRequestBody(SrProperties properties) {
        return new DecryptRequestBody(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "simple-restful", name = "global-encryption", havingValue = "true")
    public SrEncryptionFilter encryptionFilter(SrProperties properties) {
        return new SrEncryptionFilter(properties.getSkipPaths(), properties.getEncryptMode(), properties.getPrivateKey());
    }

    @Bean
    @ConditionalOnBean(SrEncryptionFilter.class)
    public FilterRegistrationBean<SrEncryptionFilter> registerAuthFilter(SrEncryptionFilter encryptionFilter) {
        FilterRegistrationBean<SrEncryptionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(encryptionFilter);
        registration.addUrlPatterns("/*");
        registration.setName("EncryptionFilter");
        registration.setOrder(0);
        return registration;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("simple restful loaded..");
    }
}
