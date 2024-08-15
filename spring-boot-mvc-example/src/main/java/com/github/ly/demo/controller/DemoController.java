package com.github.ly.demo.controller;

import com.github.ly.annotation.Decrypt;
import com.github.ly.constant.SrConstant;
import com.github.ly.demo.model.User;
import com.github.ly.demo.model.vo.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
@Tag(name = "测试接口", description = "先对代码做个测试")
public interface DemoController {
    @GetMapping("hello")
    @Operation(summary = "hello")
	String hello();

    @GetMapping("privacy")
    @Operation(summary = "privacy")
	User privacy();

    @PostMapping("decrypt/post")
    @Operation(summary = "post-decrypt")
	String decryptPost(@RequestBody @Decrypt ReqUser user);

    @GetMapping("decrypt/get")
    @Operation(summary = "get-decrypt")
    @Parameter(name = SrConstant.DECRYPT_PARAM_NAME, example = "JCNFRDI1RkNBRER7InRlc3QiOiJ6aGFuZyBzYW4ifQ==")
    @Decrypt
	String decryptGet(@RequestAttribute("test") String test);
}
