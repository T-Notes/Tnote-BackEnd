package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.dto.SignInResponse;
import com.example.tnote.boundedContext.user.dto.TokenRequest;
import com.example.tnote.boundedContext.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/refresh")
    public ResponseEntity<Result> refreshToken(@RequestBody TokenRequest tokenRequest){
        log.info("----- AuthController refreshToken -----");
        log.info("TokenRequest.registrationId={}", tokenRequest.getRegistrationId());
        log.info("TokenRequest.refreshToken={}", tokenRequest.getRefreshToken());

        SignInResponse response = authService.refreshToken(tokenRequest);

        return ResponseEntity.ok(Result.of(response));
    }
}
