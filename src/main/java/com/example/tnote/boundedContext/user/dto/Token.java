package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Token {
    private String accessToken;
    private String refreshToken;
    private String key;
}