package com.github.ly.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WebMvcConfig {
    @Value("${simple-restful.private-key:}")
    private String privateKey;
}
