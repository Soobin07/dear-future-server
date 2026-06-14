package com.dearfuture.capsule.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dearfuture.capsule.dto.CapsuleCreateRequest;
import com.dearfuture.capsule.dto.CapsuleCreateResponse;
import com.dearfuture.capsule.dto.CapsuleDetailResponse;
import com.dearfuture.capsule.dto.CapsuleListResponse;
import com.dearfuture.capsule.dto.CapsuleStatus;
import com.dearfuture.capsule.dto.CapsuleUpdateRequest;
import com.dearfuture.capsule.dto.CapsuleUpdateResponse;
import com.dearfuture.capsule.service.CapsuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/capsules")
@RequiredArgsConstructor
@Tag(name = "Capsule", description = "타임캡슐 API")
public class CapsuleController {

    private final CapsuleService capsuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "캡슐 생성",
            description = "미래에 열람할 타임캡슐을 생성합니다."
    )
    public CapsuleCreateResponse createCapsule(
            Authentication authentication,
            @Valid @RequestBody CapsuleCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.createCapsule(userId, request);
    }
    
    @GetMapping
    public List<CapsuleListResponse> getMyCapsules(
            Authentication authentication,
            @RequestParam(required = false) CapsuleStatus status
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.getMyCapsules(userId, status);
    }
    
    @Operation(
            summary = "내 캡슐 목록 조회",
            description = "현재 로그인한 사용자의 캡슐 목록을 조회합니다."
    )
    @GetMapping
    public List<CapsuleListResponse> getMyCapsules(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.getMyCapsules(userId);
    }
    
    @Operation(
            summary = "캡슐 상세 조회",
            description = "오픈 날짜가 지난 캡슐만 열람할 수 있습니다."
    )
    @GetMapping("/{capsuleId}")
    public CapsuleDetailResponse getCapsule(
            Authentication authentication,
            @PathVariable("capsuleId") Long capsuleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.getCapsule(userId, capsuleId);
    }
    
    @Operation(
            summary = "캡슐 수정",
            description = "본인이 작성한 캡슐만 수정할 수 있습니다."
    )
    @PutMapping("/{capsuleId}")
    public CapsuleUpdateResponse updateCapsule(
            Authentication authentication,
            @PathVariable("capsuleId") Long capsuleId,
            @Valid @RequestBody CapsuleUpdateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        return capsuleService.updateCapsule(userId, capsuleId, request);
    }
    
    @Operation(
            summary = "캡슐 삭제",
            description = "본인이 작성한 캡슐만 삭제할 수 있습니다."
    )
    @DeleteMapping("/{capsuleId}")
    public ResponseEntity<Void> deleteCapsule(
            Authentication authentication,
            @PathVariable("capsuleId") Long capsuleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        capsuleService.deleteCapsule(userId, capsuleId);

        return ResponseEntity.noContent().build();
    }
}