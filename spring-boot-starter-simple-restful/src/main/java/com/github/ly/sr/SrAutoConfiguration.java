package com.github.ly.sr;

import com.github.ly.sr.exception.SrExceptionAdvice;
import com.github.ly.sr.response.SrResponseBodyAdvice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
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

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("simple restful loaded..");
    }
}
