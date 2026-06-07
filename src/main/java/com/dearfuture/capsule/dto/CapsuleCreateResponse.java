package com.dearfuture.capsule.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapsuleCreateResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime openAt;
    private LocalDateTime createdAt;
}