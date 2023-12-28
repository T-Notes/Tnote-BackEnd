package com.example.tnote.boundedContext.classLog.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/classLog")
@RequiredArgsConstructor
public class ClassLogController {
    private final ClassLogService classLogService;
    @PostMapping("/write")
    public ResponseEntity<Result> createClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestBody ClassLogRequest classLogRequest) {
        // 여기서 PrincipalDetails 객체를 출력해 확인
        log.info("PrincipalDetails in Controller: {}", principalDetails);
        if (principalDetails == null) {
            log.warn("PrincipalDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }
        Long userId = principalDetails.getId();
        ClassLogResponse classLogResponse = classLogService.save(userId, classLogRequest);

        return ResponseEntity.ok(Result.of(classLogResponse));
    }
}
