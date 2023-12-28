package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import com.example.tnote.boundedContext.user.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleRequestService implements RequestService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;
    private final UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String GRANT_TYPE;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.google.token_uri}")
    private String TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String USER_INFO_URI;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;

    @Transactional
    @Override
    public SignInResponse redirect(TokenRequest tokenRequest) {
        // 구글에서 넘겨준 엑세스 토큰
        TokenResponse tokenResponse = getToken(tokenRequest);
        // 구글에서 넘겨준 유저 정보
        GoogleUserInfo googleUserInfo = getUserInfo(tokenResponse.getAccessToken());

        User user = userRepository.findByEmail(googleUserInfo.getEmail()).orElse(null);

        // 회원 가입이 안되어있는 경우(최초 로그인 시)
        if(user == null){
            user = UserResponse.toEntity(userService.signUp(googleUserInfo.getEmail(), googleUserInfo.getName()));
        }

        // 회원 가입이 되어있는 경우
        // 서버에서 생성한 jwt 토큰
        Token token = jwtTokenProvider.createToken(googleUserInfo.getEmail());

        // 서버에 해당 이메일로 저장된 리프레시 토큰이 없으면 저장(== 첫 회원가입 시 -> 이후에는 리프레시 토큰 검증을 통해 재발급 및 저장함)
        if(!refreshTokenRepository.existsByKeyEmail(user.getEmail())) {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .keyEmail(user.getEmail())
                    .refreshToken(token.getRefreshToken())
                    .build();

            refreshTokenRepository.save(newRefreshToken);
        }

        return SignInResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse getToken(TokenRequest tokenRequest) {
        return webClient.post()
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("client_id", CLIENT_ID)
                        .with("client_secret", CLIENT_SECRET)
                        .with("code", tokenRequest.getCode())
                        .with("redirect_uri", REDIRECT_URI)
                        .with("grant_type", GRANT_TYPE))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

    }

    @Override
    public GoogleUserInfo getUserInfo(String accessToken) {
        return webClient.get()
                .uri(USER_INFO_URI)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfo.class)
                .block();
    }

}