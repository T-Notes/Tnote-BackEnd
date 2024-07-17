package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponse {

    private KakaoUserInfo kakaoUserInfo;
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String oauthRefreshToken;
}