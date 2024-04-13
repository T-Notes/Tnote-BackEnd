package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUserInfo;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.TokenResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoRequestService implements RequestService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;

    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token_uri}")
    private String TOKEN_URI;

    @Override
    public JwtResponse redirect(String provider, String code, String state) {
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

            //return getBuild(newToken_AccessToken.getAccessToken(), newRefreshToken.getRefreshToken(), user);
            return JwtResponse.builder()
                    .accessToken(newToken_AccessToken.getAccessToken())
                    .refreshToken(newRefreshToken.getRefreshToken())
                    .userId(user.getId())
                    .build();
        }

        RefreshToken refreshToken = refreshTokenRepository.findByKeyEmail(user.getEmail())
                .orElseThrow(() -> CustomException.WRONG_REFRESH_TOKEN);

        //return getBuild(newToken_AccessToken.getAccessToken(), refreshToken.getRefreshToken(), user);
        return JwtResponse.builder()
                .accessToken(newToken_AccessToken.getAccessToken())
                .refreshToken(refreshToken.getRefreshToken())
                .userId(user.getId())
                .build();
    }

    private JwtResponse getBuild(String accessToken, String refreshToken, User user) {
        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse getToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", GRANT_TYPE);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("client_id", CLIENT_ID);
        formData.add("code", code);

        return webClient.mutate()
                .baseUrl(TOKEN_URI)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    @Override
    public KakaoUserInfo getUserInfo(String accessToken) {
        return webClient.mutate()
                .baseUrl("https://kapi.kakao.com")
                .build()
                .get()
                .uri("/v2/user/me")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }

    @Override
    public KakaoUnlinkResponse unLink(String accessToken) {
        return webClient.mutate()
                .baseUrl("https://kapi.kakao.com")
                .build()
                .get()
                .uri("/v1/user/unlink")
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(KakaoUnlinkResponse.class)
                .block();
    }
}