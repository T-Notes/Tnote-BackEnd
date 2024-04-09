package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnlinkRequest {
    private String accessToken; // 차후 google 로그인 연동 이후 사용

    // kakao, google 연결 끊기
    public static UnlinkRequest createWithAccessToken(String accessToken) {
        return UnlinkRequest.builder().accessToken(accessToken).build();
    }
}
