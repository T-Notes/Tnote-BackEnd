package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.dto.UserRequest;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PatchMapping("/{userId}")
    public ResponseEntity<Result> updateExtraInfo(@PathVariable Long userId, @RequestBody UserRequest dto) throws IOException {
        log.info("user 추가 정보 등록 / 수정 같은 api 사용");
        UserResponse response = userService.updateExtraInfo(userId, dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Result> logout(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @AuthenticationPrincipal PrincipalDetails user) {

        log.info("PrincipalDetails in user - controller : {}", user);

        if (user == null) {
            log.warn("PrincipalDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }

        //userService.logout(request, response, user);

        return ResponseEntity.ok(Result.of("로그아웃 되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<Result> deleteUser(@AuthenticationPrincipal PrincipalDetails user) {
        log.info("로그인한 user만 탈퇴 진행 가능");

        log.info("user : {}", user);

        //userService.deleteUser(user);

        return ResponseEntity.ok(Result.of("탈퇴 처리가 완료 되었습니다."));
    }
}
