package com.dearfuture.capsule.dto;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class CapsuleCreateRequest {

    private String title;
    private String content;
    private LocalDateTime openAt;
}