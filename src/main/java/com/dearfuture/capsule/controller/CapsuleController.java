package com.dearfuture.capsule.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.dearfuture.capsule.dto.CapsuleCreateRequest;
import com.dearfuture.capsule.dto.CapsuleCreateResponse;
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
}