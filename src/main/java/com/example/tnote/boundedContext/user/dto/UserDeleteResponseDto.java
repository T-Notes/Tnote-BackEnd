package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponseDto {
    private Long id;

    public static UserDeleteResponseDto from(KakaoUnlinkResponse unlink) {
        return UserDeleteResponseDto.builder()
                .id(unlink.getId())
                .build();
    }
}
