package com.dearfuture.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginTokenResult {

    private String accessToken;
    private String refreshToken;
}