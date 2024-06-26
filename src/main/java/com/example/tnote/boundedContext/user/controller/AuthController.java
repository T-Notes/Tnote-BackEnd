package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.dto.JwtResponse;
import com.example.tnote.boundedContext.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        JwtResponse jwt = authService.redirect(registrationId, code, state);

        return ResponseEntity.ok(Result.of(jwt));
    }

    @GetMapping("/tnote/refresh")
    public ResponseEntity<Result> refreshToken(HttpServletRequest request) {
        JwtResponse response = authService.refreshToken(request.getHeader("RefreshToken"));

        return ResponseEntity.ok(Result.of(response));
    }

}
