package com.github.ly.demo.service;

import com.github.ly.annotation.Decrypt;
import com.github.ly.demo.controller.DemoController;
import com.github.ly.demo.model.User;
import com.github.ly.demo.model.vo.ReqUser;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoControllerImpl implements DemoController {
	public String hello() {
		return "hello world";
	}

	public User privacy() {
		return User.of("Halo.Chen");
	}

	public String decryptPost(@RequestBody @Decrypt ReqUser user) {
		return "hello " + user.getUsername();
	}

	public String decryptGet(@RequestAttribute("test") String test) {
		return "hello " + test;
	}
}
