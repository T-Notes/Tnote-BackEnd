package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignInResponse {

    private GoogleUserInfo googleUserInfo;
    private String accessToken;
    private String refreshToken;
    private Long userId;
}
