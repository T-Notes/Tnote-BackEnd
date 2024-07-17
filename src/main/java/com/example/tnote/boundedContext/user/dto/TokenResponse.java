package com.example.tnote.boundedContext.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenResponse {

    private String tokenType;
    private String accessToken; // oauth access token
    private Integer expiresIn; // oauth access token expire time
    private String refreshToken; // oauth refresh token
    private Integer refreshTokenExpiresIn; // oauth refresh token expire time
    private String error;
    private String errorDescription;
}
