package com.dearfuture.auth.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dearfuture.auth.dto.LoginRequest;
import com.dearfuture.auth.dto.LoginResponse;
import com.dearfuture.auth.dto.LoginTokenResult;
import com.dearfuture.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인 후 JWT 토큰을 발급합니다."
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginTokenResult tokenResult = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenResult.getRefreshToken())
                .httpOnly(true)
                .secure(false) // 로컬 개발은 false, 배포 HTTPS에서는 true
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofDays(14))
                .build();

        LoginResponse response = LoginResponse.builder()
                .accessToken(tokenResult.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
}