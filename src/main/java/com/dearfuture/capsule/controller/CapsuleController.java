package com.dearfuture.capsule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dearfuture.capsule.dto.CapsuleCreateRequest;
import com.dearfuture.capsule.dto.CapsuleCreateResponse;
import com.dearfuture.capsule.dto.CapsuleDetailResponse;
import com.dearfuture.capsule.dto.CapsuleListResponse;
import com.dearfuture.capsule.service.CapsuleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/capsules")
@RequiredArgsConstructor
public class CapsuleController {

    private final CapsuleService capsuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CapsuleCreateResponse createCapsule(
            Authentication authentication,
            @RequestBody CapsuleCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.createCapsule(userId, request);
    }
    
    @GetMapping
    public List<CapsuleListResponse> getMyCapsules(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.getMyCapsules(userId);
    }
    
    @GetMapping("/{capsuleId}")
    public CapsuleDetailResponse getCapsule(
            Authentication authentication,
            @PathVariable("capsuleId") Long capsuleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.getCapsule(userId, capsuleId);
    }
}