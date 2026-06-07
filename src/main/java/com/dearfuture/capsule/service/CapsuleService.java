package com.dearfuture.capsule.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dearfuture.capsule.dto.CapsuleCreateRequest;
import com.dearfuture.capsule.dto.CapsuleCreateResponse;
import com.dearfuture.capsule.entity.Capsule;
import com.dearfuture.capsule.repository.CapsuleRepository;
import com.dearfuture.user.entity.User;
import com.dearfuture.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    public CapsuleCreateResponse createCapsule(Long userId, CapsuleCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다."
                ));

        Capsule capsule = Capsule.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .openAt(request.getOpenAt())
                .createdAt(LocalDateTime.now())
                .build();

        Capsule savedCapsule = capsuleRepository.save(capsule);

        return CapsuleCreateResponse.builder()
                .id(savedCapsule.getId())
                .title(savedCapsule.getTitle())
                .content(savedCapsule.getContent())
                .openAt(savedCapsule.getOpenAt())
                .createdAt(savedCapsule.getCreatedAt())
                .build();
    }
}