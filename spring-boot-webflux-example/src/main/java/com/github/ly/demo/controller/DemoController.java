package com.github.ly.demo.controller;

import com.github.ly.demo.model.User;
import com.github.ly.demo.model.vo.ReqUser;
import com.github.ly.sr.SrConstant;
import com.github.ly.sr.encryption.method.Decrypt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("decrypt/post")
    @Operation(summary = "post-decrypt")
    public String decryptPost(@RequestBody @Decrypt ReqUser user) {
        return "hello " + user.getUsername();
    }

    @GetMapping("decrypt/get")
    @Operation(summary = "get-decrypt")
    @Parameter(name = SrConstant.DECRYPT_PARAM_NAME, example = "JCNFRDI1RkNBRER7InRlc3QiOiJ6aGFuZyBzYW4ifQ==")
    @Decrypt
    public String decryptGet(@RequestAttribute("test") String test) {
        return "hello " + test;
    }
}