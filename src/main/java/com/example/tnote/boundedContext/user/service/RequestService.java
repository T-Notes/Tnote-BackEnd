package com.example.tnote.boundedContext.user.service;

import com.example.tnote.boundedContext.user.dto.SignInResponse;
import com.example.tnote.boundedContext.user.dto.TokenRequest;
import com.example.tnote.boundedContext.user.dto.TokenResponse;
import com.example.tnote.boundedContext.user.dto.UnlinkRequest;

public interface RequestService<T> {
    SignInResponse redirect(TokenRequest tokenRequest);

    TokenResponse getToken(TokenRequest tokenRequest);

    T getUserInfo(String accessToken);

    void unLink(UnlinkRequest request);
}
