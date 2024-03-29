package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.service.RefreshTokenService;
import com.example.tnote.boundedContext.user.dto.SignInResponse;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.TokenRequest;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoRequestService kakaoRequestService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignInResponse redirect(TokenRequest tokenRequest) {

        return kakaoRequestService.redirect(tokenRequest);
    }

    @Transactional
    public SignInResponse refreshToken(String refreshToken) {

        if (jwtTokenProvider.isExpired(refreshToken)) {
            // refresh token 만료시 재로그인 필요
            throw CustomException.EXPIRED_REFRESH_TOKEN;
        }

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);

        User user = getUserFromRefreshToken(refreshTokenObj);

        Token newToken = jwtTokenProvider.createAccessToken(user.getEmail());

        return buildSignInResponse(newToken.getAccessToken(), refreshToken, user.getId());
    }

    private User getUserFromRefreshToken(RefreshToken refreshToken) {
        return userRepository.findByEmail(refreshToken.getKeyEmail())
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);
    }

    private SignInResponse buildSignInResponse(String accessToken, String refreshToken, Long userId) {
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }
}
