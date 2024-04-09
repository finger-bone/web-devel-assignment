package com.webdev.backend.controller;

import com.webdev.backend.model.User;
import com.webdev.backend.service.EmployeeService;
import com.webdev.backend.service.UserService;
import com.webdev.backend.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
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
    public boolean getMethodName(@RequestParam String username) {
        return userService.userExists(username);
    }
    

    @GetMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        try {
            if(userService.verify(username, password)) {
                return JwtUtil.createToken(userService.getByUsername(username));
            } else {
                // return unauthorized status code
                return null;
            }
        }
        catch(Exception e) {
            return null;
        }
    }
}