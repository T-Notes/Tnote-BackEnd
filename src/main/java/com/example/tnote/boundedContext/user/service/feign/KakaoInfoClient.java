package com.example.tnote.boundedContext.user.service.feign;


import com.example.tnote.boundedContext.user.dto.KakaoUnlinkResponse;
import com.example.tnote.boundedContext.user.dto.KakaoUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-info", url = "https://kapi.kakao.com")
public interface KakaoInfoClient {

    @GetMapping("/v2/user/me")
    KakaoUserInfo getUserInfo(@RequestHeader("Authorization") String authorization);

    @PostMapping(value = "/v1/user/unlink")
    KakaoUnlinkResponse unlink(@RequestHeader("Authorization") String authorization);
}
