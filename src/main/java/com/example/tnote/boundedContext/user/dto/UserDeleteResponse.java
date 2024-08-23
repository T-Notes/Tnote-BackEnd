package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponse {
    private Long id;

    public static UserDeleteResponse from(KakaoUnlinkResponse unlink) {
        return UserDeleteResponse.builder()
                .id(unlink.getId())
                .build();
    }
}
