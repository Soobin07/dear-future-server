package com.dearfuture.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AuthTestController {

    @GetMapping("/api/auth/me")
    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자 정보를 조회합니다."
    )
    public String me(Authentication authentication) {
        return "userId = " + authentication.getPrincipal();
    }
}