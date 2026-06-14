package com.dearfuture.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dearfuture.auth.dto.LoginRequest;
import com.dearfuture.auth.dto.LoginResponse;
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
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}