package com.dearfuture.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupResponse {

    private Long id;

    private String email;

    private String nickname;
}