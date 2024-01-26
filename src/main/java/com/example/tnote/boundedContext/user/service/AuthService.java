package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.service.RefreshTokenService;
import com.example.tnote.boundedContext.user.dto.RefreshTokenDto;
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
    public SignInResponse refreshToken(RefreshTokenDto dto) {
        String refreshToken = dto.getRefreshToken();

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);

        User user = userRepository.findByEmail(refreshTokenObj.getKeyEmail())
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        // 유효한 리프레시 토큰이면, 새로운 액세스 토큰 생성 및 반환
        if (jwtTokenProvider.validateRefreshToken(refreshTokenObj)) {
            log.info("유효한 Refresh Token 입니다.");
            return SignInResponse.builder()
                    .accessToken(jwtTokenProvider.recreationAccessToken(user.getEmail()))
                    .refreshToken(refreshTokenObj.getRefreshToken())
                    .userId(user.getId())
                    .build();
        }
        // 유효하지 않은 리프레시 토큰이면, 새로운 토큰 생성 및 반환
        else {
            log.info("유효하지 않은 Refresh Token 입니다.");
            Token newToken = jwtTokenProvider.createToken(user.getEmail());

            refreshTokenService.save(newToken.getRefreshToken(), user.getEmail());

            return SignInResponse.builder()
                    .accessToken(newToken.getAccessToken())
                    .refreshToken(newToken.getRefreshToken())
                    .userId(user.getId())
                    .build();
        }
    }
}
