package com.github.ly.sr;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SrProperties.class)
public class SrRequestConfiguration implements WebMvcConfigurer {


}
