package com.github.ly.demo.controller;

import com.github.ly.demo.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
@Tag(name = "测试接口", description = "先对代码做个测试")
public class DemoController {

    @GetMapping("hello")
    @Operation(summary = "hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("privacy")
    @Operation(summary = "privacy")
    public User privacy() {
        return User.of("Halo.Chen");
    }
}
