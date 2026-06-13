package com.dearfuture.capsule.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CapsuleUpdateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "공개일은 필수입니다.")
    @Future(message = "공개일은 미래 시간이어야 합니다.")
    private LocalDateTime openAt;
}