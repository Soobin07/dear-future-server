package com.dearfuture.auth.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.dearfuture.auth.dto.LoginRequest;
import com.dearfuture.auth.dto.LoginTokenResult;
import com.dearfuture.auth.entity.RefreshToken;
import com.dearfuture.auth.repository.RefreshTokenRepository;
import com.dearfuture.global.security.JwtTokenProvider;
import com.dearfuture.user.entity.User;
import com.dearfuture.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.dearfuture.auth.dto.TokenRefreshResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginTokenResult login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        LocalDateTime expiresAt = jwtTokenProvider.getExpiration(refreshToken)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        savedToken -> savedToken.update(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .user(user)
                                        .token(refreshToken)
                                        .expiresAt(expiresAt)
                                        .build()
                        )
                );

        return LoginTokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    public TokenRefreshResponse refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh Token이 없습니다."
            );
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 Refresh Token입니다."
            );
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "저장되지 않은 Refresh Token입니다."
                ));

        User user = savedToken.getUser();

        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}