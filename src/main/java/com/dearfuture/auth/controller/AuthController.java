package com.dearfuture.auth.controller;

import org.springframework.web.bind.annotation.*;

import com.dearfuture.auth.dto.LoginRequest;
import com.dearfuture.auth.dto.LoginResponse;
import com.dearfuture.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}