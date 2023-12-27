package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtResponse {

    private Long userId;
    private String accessToken;
    private String refreshToken;
}
