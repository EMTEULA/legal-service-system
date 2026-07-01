package com.legal.controller;

import com.legal.common.Result;
import com.legal.service.LegalService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {
    private final LegalService service;

    public AuthController(LegalService service) { this.service = service; }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> body) {
        return Result.success("登录成功", service.login(body));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, Object> body) {
        service.register(body);
        return Result.success("注册成功", null);
    }
}
