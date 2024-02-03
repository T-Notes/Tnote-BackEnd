package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshTokenDto {
    private String registrationId;
    private String refreshToken;
}
