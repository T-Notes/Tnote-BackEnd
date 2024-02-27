package com.example.tnote.boundedContext.user.service;

import static com.example.tnote.base.exception.user.UserErrorResult.USER_NOT_FOUND;

import com.example.tnote.base.exception.jwt.JwtErrorResult;
import com.example.tnote.base.exception.jwt.JwtException;
import com.example.tnote.base.exception.user.UserException;
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
    public SignInResponse refreshToken(String accessToken, String refreshToken) {
        if (!jwtTokenProvider.isExpired(accessToken)) {
            throw new JwtException(JwtErrorResult.NOT_EXPIRED_TOKEN);
        }

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);

        User user = getUserFromRefreshToken(refreshTokenObj);

        Token newToken = jwtTokenProvider.createToken(user.getEmail());

        // 24 * 60 * 60 * 1000L
        if (!jwtTokenProvider.isExpired(refreshToken)) {
            return buildSignInResponse(newToken.getAccessToken(), refreshToken, user.getId());

        } else {
            refreshTokenService.save(newToken.getRefreshToken(), user.getEmail());

            return buildSignInResponse(newToken.getAccessToken(), newToken.getRefreshToken(), user.getId());
        }
    }

    private User getUserFromRefreshToken(RefreshToken refreshToken) {
        return userRepository.findByEmail(refreshToken.getKeyEmail())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    private SignInResponse buildSignInResponse(String accessToken, String refreshToken, Long userId) {
        return SignInResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }
}
