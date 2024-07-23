package com.github.ly.demo.config;

import com.github.ly.sr.encryption.method.DecryptGetParam;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@EnableKnife4j
@Configuration(proxyBeanMethods = false)
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Value("${simple-restful.private-key:}")
    private String privateKey;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DecryptGetParam(privateKey))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/doc.html", "/swagger-ui*/**");
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
