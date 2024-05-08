package com.webdev.backend.controller;

import com.webdev.backend.model.User;
import com.webdev.backend.service.EmployeeService;
import com.webdev.backend.service.UserService;
import com.webdev.backend.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	@Operation(summary = "注册新用户", description = "提供用户名和密码来注册新用户。")
	public User registerUser(@RequestParam String username, @RequestParam String password) {
		User user = new User();
		if (userService.userExists(username)) {
			return null;
		}
		user.setUsername(username);
		user.setPassword(password);
		user.setRole("user");
		return userService.registerUser(user);
	}

	@GetMapping("/valid/{username}")
	@Operation(summary = "检查用户名是否可用", description = "提供用户名来检查其有效性。")
	public boolean validateUsername(@PathVariable String username, @RequestParam(required = false) String excluded) {
		if(username.equals("admin")) {
			return false;
		}
		else if (username.equals(excluded)) {
			return true;
		} else {
			return !userService.userExists(username);
		}
	}

	@GetMapping("/login")
	@Operation(summary = "用户登录", description = "提供用户名和密码来登录，获得 JWT token。")
	public String login(@RequestParam String username, @RequestParam String password) {
		try {
			if (userService.verify(username, password)) {
				return JwtUtil.createToken(userService.getByUsername(username));
			}
			else {
				return null;
			}
		}
		catch (Exception e) {
			return null;
		}
	}
}