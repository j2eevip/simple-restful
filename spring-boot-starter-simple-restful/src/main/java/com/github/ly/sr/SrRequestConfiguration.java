package com.github.ly.sr;

import com.github.ly.sr.encryption.method.DecryptGetParam;
import com.github.ly.sr.encryption.method.DecryptRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SrProperties.class)
public class SrRequestConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private SrProperties properties;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DecryptGetParam(properties.getPrivateKey()))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/doc.html", "/swagger-ui*/**");
    }

    @Bean
    public DecryptRequestBody decryptRequestBody() {
        return new DecryptRequestBody(properties.getPrivateKey());
    }
}
