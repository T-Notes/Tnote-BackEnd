package com.example.tnote.boundedContext.user.service;

import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.TokenResponse;

public interface RequestService<T> {
    JwtResponse redirect(String provider, String code, String state);

    TokenResponse getToken(String code);

    T getUserInfo(String accessToken);

    KakaoUnlinkResponse unLink(String accessToken);
}
