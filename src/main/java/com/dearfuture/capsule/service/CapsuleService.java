package com.dearfuture.capsule.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.dearfuture.capsule.dto.CapsuleCreateRequest;
import com.dearfuture.capsule.dto.CapsuleCreateResponse;
import com.dearfuture.capsule.dto.CapsuleDetailResponse;
import com.dearfuture.capsule.dto.CapsuleListResponse;
import com.dearfuture.capsule.dto.CapsuleStatus;
import com.dearfuture.capsule.dto.CapsuleUpdateRequest;
import com.dearfuture.capsule.dto.CapsuleUpdateResponse;
import com.dearfuture.capsule.entity.Capsule;
import com.dearfuture.capsule.repository.CapsuleRepository;
import com.dearfuture.user.entity.User;
import com.dearfuture.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    @Transactional
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
                .isOpened(false)
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
    
    public List<CapsuleListResponse> getMyCapsules(Long userId) {

        List<Capsule> capsules = capsuleRepository.findAllByUserId(userId);

        return capsules.stream()
                .map(capsule -> CapsuleListResponse.builder()
                        .id(capsule.getId())
                        .title(capsule.getTitle())
                        .openAt(capsule.getOpenAt())
                        .createdAt(capsule.getCreatedAt())
                        .build())
                .toList();
    }
    
    public CapsuleDetailResponse getCapsule(Long userId, Long capsuleId) {

        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "캡슐을 찾을 수 없습니다."
                ));

        if (!capsule.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "해당 캡슐에 접근할 수 없습니다."
            );
        }
        
        if (!capsule.getIsOpened()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "아직 열 수 없는 캡슐입니다."
            );
        }
        
        return CapsuleDetailResponse.builder()
                .id(capsule.getId())
                .title(capsule.getTitle())
                .content(capsule.getContent())
                .openAt(capsule.getOpenAt())
                .createdAt(capsule.getCreatedAt())
                .updatedAt(capsule.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public CapsuleUpdateResponse updateCapsule(
            Long userId,
            Long capsuleId,
            CapsuleUpdateRequest request
    ) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "캡슐을 찾을 수 없습니다."
                ));

        if (!capsule.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "해당 캡슐을 수정할 수 없습니다."
            );
        }

        capsule.update(
                request.getTitle(),
                request.getContent(),
                request.getOpenAt()
        );

        return CapsuleUpdateResponse.builder()
                .id(capsule.getId())
                .title(capsule.getTitle())
                .content(capsule.getContent())
                .openAt(capsule.getOpenAt())
                .updatedAt(capsule.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public void deleteCapsule(Long userId, Long capsuleId) {

        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "캡슐을 찾을 수 없습니다."
                ));

        if (!capsule.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "해당 캡슐을 삭제할 수 없습니다."
            );
        }

        capsuleRepository.delete(capsule);
    }
    
    public List<CapsuleListResponse> getMyCapsules(Long userId, CapsuleStatus status) {

        List<Capsule> capsules;

        if (status == null) {
            capsules = capsuleRepository.findAllByUserId(userId);
        } else if (status == CapsuleStatus.OPENED) {
            capsules = capsuleRepository.findAllByUserIdAndIsOpenedTrue(userId);
        } else {
            capsules = capsuleRepository.findAllByUserIdAndIsOpenedFalse(userId);
        }

        return capsules.stream()
                .map(capsule -> CapsuleListResponse.builder()
                        .id(capsule.getId())
                        .title(capsule.getTitle())
                        .openAt(capsule.getOpenAt())
                        .createdAt(capsule.getCreatedAt())
                        .build())
                .toList();
    }
}