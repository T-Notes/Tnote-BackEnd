package com.example.tnote.boundedContext.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class TokenRequest {

    private String registrationId;
    private String code;
    private String state;
    private String refreshToken;
}
