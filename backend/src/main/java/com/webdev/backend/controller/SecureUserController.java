package com.webdev.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webdev.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/secure/user")
@SecurityRequirement(name = "bearerAuth")
public class SecureUserController {
    @Autowired
    UserService userService;

    @GetMapping("/verify")
	@Operation(summary = "验证密码", description = "提供用户名和密码来验证密码是否正确。")
	public boolean verify(@RequestAttribute("username") String username, @RequestParam String password) {
		return userService.verify(username, password);
	}

	@GetMapping("/ping")
	@Operation(summary = "测试JWT是否有效", description = "测试JWT是否有效")
	public String ping(@RequestAttribute("username") String username) {
		if(username != null) {
			return "pong";
		}
		else {
			return null;
		}
	}
	
	@PutMapping("/reset")
    @Operation(summary = "重置密码", description = "提供用户名、旧密码和新密码来重置密码。")
	public void ResetPassword(@RequestAttribute("username") String username, @RequestParam String password, @RequestParam String newPassword) {
		if (userService.verify(username, password)) {
			userService.resetPassword(username, newPassword);
		}
	}
}
