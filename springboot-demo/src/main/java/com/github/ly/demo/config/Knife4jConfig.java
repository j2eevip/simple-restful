package com.github.ly.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info contact = new Info().title("OBOX RESTful APIs")
                .description("OBOX后台RESTful接口Swagger帮助文档")
                .version("1.0.0")
                .contact(new Contact().name("halo").email("halo.chen@halooffice.com"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));
        return new OpenAPI().info(contact);
    }
}
