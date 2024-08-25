package com.example.tnote.boundedContext.RefreshToken.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshToken {
    private String registrationId;
    private String refreshToken;
}
