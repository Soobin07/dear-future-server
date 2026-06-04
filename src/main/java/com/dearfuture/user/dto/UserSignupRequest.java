package com.dearfuture.user.dto;

import lombok.Getter;

@Getter
public class UserSignupRequest {

    private String email;

    private String password;

    private String nickname;
}