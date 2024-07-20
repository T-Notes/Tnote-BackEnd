package com.example.tnote.boundedContext.user.service;

import static com.example.tnote.boundedContext.user.exception.UserErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.RefreshToken.service.RefreshTokenService;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.OauthRefreshDto;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.UserDeleteResponseDto;
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
    public JwtResponse redirect(String provider, String code, String state) {
        return kakaoRequestService.redirect(provider, code, state);
    }

    @Transactional
    public JwtResponse refreshToken(String refreshToken) {

        if (jwtTokenProvider.isExpired(refreshToken)) {
            // refresh token 만료시 재로그인 필요
            throw new CustomException(EXPIRED_REFRESH_TOKEN);
        }

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);

        User user = getUserFromRefreshToken(refreshTokenObj);

        Token newToken = jwtTokenProvider.createAccessToken(user.getEmail());

        return buildSignInResponse(newToken.getAccessToken(), refreshToken, user.getId());
    }

    private User getUserFromRefreshToken(RefreshToken refreshToken) {
        return userRepository.findByEmail(refreshToken.getKeyEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private JwtResponse buildSignInResponse(String accessToken, String refreshToken, Long userId) {
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build();
    }

    @Transactional
    public UserDeleteResponseDto deleteUser(Long userId, String oauthRefreshToken) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        deleteAll(currentUser);

        OauthRefreshDto dto = kakaoRequestService.refresh(oauthRefreshToken);

        KakaoUnlinkResponse unlink = kakaoRequestService.unLink(dto.getAccess_token());

        return UserDeleteResponseDto.of(unlink);
    }

    // 연관키로 묶여 있음
    private void deleteAll(User currentUser) {
        refreshTokenRepository.deleteByKeyEmail(currentUser.getEmail());
        userRepository.delete(currentUser);
    }

}
