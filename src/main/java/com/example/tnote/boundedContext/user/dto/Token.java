package com.example.tnote.boundedContext.user.dto;

import lombok.*;

@Builder
@Getter
public class Token {
    private String accessToken;
    private String refreshToken;
    private String key;
}