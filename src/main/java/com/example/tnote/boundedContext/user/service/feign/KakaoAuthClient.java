package com.example.tnote.boundedContext.user.service.feign;


import com.example.tnote.boundedContext.user.dto.OauthRefreshDto;
import com.example.tnote.boundedContext.user.dto.TokenResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakao-auth", url = "https://kauth.kakao.com")
public interface KakaoAuthClient {

    @PostMapping("/oauth/token")
    TokenResponse getToken(@RequestParam("grant_type") String grantType,
                           @RequestParam("client_id") String clientId,
                           @RequestParam("redirect_uri") String redirectUri,
                           @RequestParam("code") String code
    );

    @PostMapping("/oauth/token")
    @Headers("Content-type: application/x-www-form-urlencoded;charset=utf-8")
    OauthRefreshDto refresh(@RequestParam("grant_type") String grantType,
                            @RequestParam("client_id") String clientId,
                            @RequestParam("refresh_token") String refreshToken);
}
