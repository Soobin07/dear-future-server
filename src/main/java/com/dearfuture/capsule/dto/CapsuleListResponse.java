package com.dearfuture.capsule.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapsuleListResponse {

    private Long id;
    private String title;
    private LocalDateTime openAt;
    private LocalDateTime createdAt;
}