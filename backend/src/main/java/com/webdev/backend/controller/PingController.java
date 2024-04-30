package com.webdev.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class PingController {

	@GetMapping("/ping")
	@Operation(summary = "访问测试", description = "正常应返回 pong 。")
	public String pong() {
		return "pong";
	}

}
