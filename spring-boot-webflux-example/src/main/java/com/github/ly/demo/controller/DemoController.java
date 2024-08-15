package com.github.ly.demo.controller;

import com.github.ly.constant.SrConstant;
import com.github.ly.demo.model.User;
import com.github.ly.demo.model.vo.ReqUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
@Tag(name = "测试接口", description = "先对代码做个测试")
public class DemoController {

    @GetMapping("hello")
    @Operation(summary = "hello")
	public Mono<String> hello() {
		return Mono.just("hello world");
    }

    @GetMapping("privacy")
    @Operation(summary = "privacy")
	public Mono<User> privacy() {
		return Mono.just(User.of("Halo.Chen"));
    }

    @PostMapping("decrypt/post")
    @Operation(summary = "post-decrypt")
	public Mono<String> decryptPost(@RequestBody ReqUser user) {
		return Mono.just("hello " + user.getUsername());
    }

    @GetMapping("decrypt/get")
    @Operation(summary = "get-decrypt")
    @Parameter(name = SrConstant.DECRYPT_PARAM_NAME, example = "JCNFRDI1RkNBRER7InRlc3QiOiJ6aGFuZyBzYW4ifQ==")
	public Mono<String> decryptGet(@RequestAttribute("test") String test) {
		return Mono.just("hello " + test);
	}

	@GetMapping("list")
	@Operation(summary = "list")
	public Flux<String> list() {
		return Flux.just("hello", "world");
    }
}
