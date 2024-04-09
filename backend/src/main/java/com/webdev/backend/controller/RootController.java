package com.webdev.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class RootController {
    @GetMapping
    public String root() {
        return "The server is up and running.";
    }

    @GetMapping("/secure")
    public String rootSecure(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("id");
        String userName = request.getAttribute("username").toString();
        String password= request.getAttribute("password").toString();
        return "当前用户信息id=" + id + ",userName=" + userName+ ",password=" + password;
    }
    
}
