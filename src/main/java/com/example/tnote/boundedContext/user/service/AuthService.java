package com.example.tnote.boundedContext.user.service;

import static com.example.tnote.boundedContext.RefreshToken.exception.RefreshTokenErrorCode.EXPIRED_REFRESH_TOKEN;

import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.exception.RefreshTokenException;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.RefreshToken.service.RefreshTokenService;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.OauthRefresh;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.UserDeleteResponse;
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
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JwtResponse redirect(final String provider, final String code, final String state) {
        return kakaoRequestService.redirect(provider, code, state);
    }

    @Transactional
    public JwtResponse refreshToken(final String refreshToken) {

        if (jwtTokenProvider.isExpired(refreshToken)) {
            // refresh token 만료시 재로그인 필요
            throw new RefreshTokenException(EXPIRED_REFRESH_TOKEN);
        }

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);

        User user = userRepository.findUserByEmail(refreshTokenObj.getKeyEmail());

        Token newToken = jwtTokenProvider.createAccessToken(user.getEmail());

        return buildSignInResponse(newToken.getAccessToken(), refreshToken, user.getId());
    }

    private JwtResponse buildSignInResponse(final String accessToken, final String refreshToken, final Long userId) {
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }

    @Transactional
    public UserDeleteResponse deleteUser(final Long userId, final String oauthRefreshToken) {

        User currentUser = userRepository.findUserById(userId);

        deleteAll(currentUser);

        OauthRefresh dto = kakaoRequestService.refresh(oauthRefreshToken);

        KakaoUnlinkResponse unlink = kakaoRequestService.unLink(dto.getAccess_token());

        return UserDeleteResponse.from(unlink);
    }

    // 연관키로 묶여 있음
    private void deleteAll(final User currentUser) {
        refreshTokenRepository.deleteByKeyEmail(currentUser.getEmail());
        userRepository.delete(currentUser);
    }

}
