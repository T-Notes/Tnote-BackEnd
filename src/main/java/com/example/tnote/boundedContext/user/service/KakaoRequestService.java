package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.jwt.JwtException;
import com.example.tnote.base.utils.JwtTokenProvider;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.dto.KakaoUserInfo;
import com.example.tnote.boundedContext.user.dto.SignInResponse;
import com.example.tnote.boundedContext.user.dto.Token;
import com.example.tnote.boundedContext.user.dto.TokenRequest;
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
    public SignInResponse redirect(TokenRequest tokenRequest) {
        // 카카오에서 넘겨준 엑세스 토큰
        TokenResponse tokenResponse = getToken(tokenRequest);
        // 카카오에서 넘겨준 유저 정보
        KakaoUserInfo kakaoUserInfo = getUserInfo(tokenResponse.getAccessToken());

        User user = userRepository.findByEmail(kakaoUserInfo.getEmail()).orElse(null);

        // 회원 가입이 되어있는 경우
        Token newToken_AccessToken = jwtTokenProvider.createAccessToken(kakaoUserInfo.getEmail());
        Token newToken_RefreshToken = jwtTokenProvider.createRefreshToken(kakaoUserInfo.getEmail());

        // 회원 가입이 안되어있는 경우(최초 로그인 시)
        if (user == null) {
            user = UserResponse.toEntity(userService.signUp(kakaoUserInfo.getEmail(), kakaoUserInfo.getName()));

            RefreshToken newRefreshToken = RefreshToken.builder()
                    .keyEmail(user.getEmail())
                    .refreshToken(newToken_RefreshToken.getRefreshToken())
                    .build();

            refreshTokenRepository.save(newRefreshToken);

            return SignInResponse.builder()
                    .accessToken(newToken_AccessToken.getAccessToken())
                    .refreshToken(newToken_RefreshToken.getRefreshToken())
                    .userId(user.getId())
                    .build();
        }
        RefreshToken refreshToken = refreshTokenRepository.findByKeyEmail(user.getEmail())
                .orElseThrow(() -> JwtException.WRONG_REFRESH_TOKEN);

//        // 서버에 해당 이메일로 저장된 리프레시 토큰이 없으면 저장(== 첫 회원가입 시 -> 이후에는 리프레시 토큰 검증을 통해 재발급 및 저장함)
//        if (!refreshTokenRepository.existsByKeyEmail(user.getEmail())) {
//
//        }

        return SignInResponse.builder()
                .accessToken(newToken_AccessToken.getAccessToken())
                .refreshToken(refreshToken.getRefreshToken())
                .userId(user.getId())
                .build();
    }

    @Override
    public TokenResponse getToken(TokenRequest tokenRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", GRANT_TYPE);
        formData.add("redirect_uri", REDIRECT_URI);
        formData.add("client_id", CLIENT_ID);
        formData.add("code", tokenRequest.getCode());

        return webClient.mutate()
                .baseUrl(TOKEN_URI)
                .build()
                .post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, response -> Mono.just(new BadRequestException()))
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
}