package com.dearfuture.user.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dearfuture.user.dto.UserSignupRequest;
import com.dearfuture.user.dto.UserSignupResponse;
import com.dearfuture.user.entity.User;
import com.dearfuture.user.entity.UserRole;
import com.dearfuture.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSignupResponse signup(UserSignupRequest request) {

    	if (userRepository.existsByEmail(request.getEmail())) {
    	    throw new ResponseStatusException(
    	            HttpStatus.CONFLICT,
    	            "이미 존재하는 이메일입니다."
    	    );
    	}

    	if (userRepository.existsByNickname(request.getNickname())) {
    	    throw new ResponseStatusException(
    	            HttpStatus.CONFLICT,
    	            "이미 존재하는 닉네임입니다."
    	    );
    	}

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return UserSignupResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .build();
    }
}