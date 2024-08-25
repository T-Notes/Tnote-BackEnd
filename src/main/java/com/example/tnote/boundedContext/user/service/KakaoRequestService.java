package com.example.tnote.boundedContext.user.service;

import static com.example.tnote.boundedContext.RefreshToken.exception.RefreshTokenErrorCode.INVALID_REFRESH_TOKEN;

import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUserInfo;
import com.example.tnote.boundedContext.user.dto.OauthRefresh;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.TokenResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import com.example.tnote.boundedContext.user.service.feign.KakaoAuthClient;
import com.example.tnote.boundedContext.user.service.feign.KakaoInfoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoRequestService implements RequestService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoInfoClient kakaoInfoClient;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token_uri}")
    private String TOKEN_URI;

    @Override
    public JwtResponse redirect(final String provider, final String code, final String state) {
        // 카카오에서 넘겨준 엑세스 토큰
        TokenResponse tokenResponse = getToken(code);

        // 카카오에서 넘겨준 유저 정보
        KakaoUserInfo kakaoUserInfo = getUserInfo(tokenResponse.getAccessToken());

        User user = userRepository.findByEmail(kakaoUserInfo.getEmail()).orElse(null);

        // 회원 가입이 되어있는 경우
        Token newToken_AccessToken = jwtTokenProvider.createAccessToken(kakaoUserInfo.getEmail());
        Token newToken_RefreshToken = jwtTokenProvider.createRefreshToken(kakaoUserInfo.getEmail());

        // 회원 가입이 안되어있는 경우(최초 로그인 시)
        if (user == null) {
            user = UserResponse.toEntity(userService.signUp(kakaoUserInfo.getEmail(), kakaoUserInfo.getName()));

            RefreshToken newRefreshToken = RefreshToken.toEntity(user.getEmail(),
                    newToken_RefreshToken.getRefreshToken());

            refreshTokenRepository.save(newRefreshToken);

            return getBuild(newToken_AccessToken.getAccessToken(), newToken_RefreshToken.getRefreshToken(), user,
                    tokenResponse.getRefreshToken());
        }

        RefreshToken refreshToken = refreshTokenRepository.findByKeyEmail(user.getEmail())
                .orElseThrow(() -> new UserException(INVALID_REFRESH_TOKEN));

        return getBuild(newToken_AccessToken.getAccessToken(), refreshToken.getRefreshToken(), user,
                tokenResponse.getRefreshToken());
    }

    private JwtResponse getBuild(final String accessToken, final String refreshToken, final User user,
                                 final String oauthRefreshToken) {
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .oauthRefreshToken(oauthRefreshToken)
                .build();
    }

    @Override
    public TokenResponse getToken(final String code) {
        return kakaoAuthClient.getToken(GRANT_TYPE, CLIENT_ID, REDIRECT_URI, code);
    }

    @Override
    public KakaoUserInfo getUserInfo(final String accessToken) {
        return kakaoInfoClient.getUserInfo("Bearer " + accessToken);
    }

    @Override
    public KakaoUnlinkResponse unLink(final String accessToken) {
        return kakaoInfoClient.unlink("Bearer " + accessToken);
    }

    public OauthRefresh refresh(final String refreshToken) {
        return kakaoAuthClient.refresh("refresh_token", CLIENT_ID, refreshToken);
    }

}