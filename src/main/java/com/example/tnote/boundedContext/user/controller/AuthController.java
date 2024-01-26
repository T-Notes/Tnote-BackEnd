package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.RefreshTokenDto;
import com.example.tnote.boundedContext.user.dto.SignInResponse;
import com.example.tnote.boundedContext.user.dto.TokenRequest;
import com.example.tnote.boundedContext.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login/oauth2/code/{registrationId}")
    public ResponseEntity<Result> redirect(
            @PathVariable("registrationId") String registrationId,
            @RequestParam("code") String code,
            @RequestParam("state") String state
    ) {
        log.info("----- AuthController.redirect -----");
        log.info("registrationId={}", registrationId);
        log.info("code={}", code);
        log.info("state={}", state);

        SignInResponse result = authService.redirect(
                TokenRequest.builder()
                        .registrationId(registrationId)
                        .code(code)
                        .state(state)
                        .build()
        );

        JwtResponse jwt = JwtResponse.builder()
                .userId(result.getUserId())
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .build();

        return ResponseEntity.ok(Result.of(jwt));
    }

    @PostMapping("/tnote/refresh")
    public ResponseEntity<Result> refreshToken(@RequestBody RefreshTokenDto dto) {
        log.info("----- AuthController refreshToken -----");
        log.info("TokenRequest.registrationId={}", dto.getRegistrationId());
        log.info("TokenRequest.refreshToken={}", dto.getRefreshToken());

        SignInResponse response = authService.refreshToken(dto);

        return ResponseEntity.ok(Result.of(response));
    }
}
