package com.dearfuture.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshResponse {

    private String accessToken;
}