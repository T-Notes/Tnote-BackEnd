package com.example.tnote.boundedContext.classLog.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<Result> createClassLog(@AuthenticationPrincipal User user,
                                                 @RequestBody ClassLogRequest classLogRequest) {
        ClassLogResponse classLogResponse = classLogService.save(user.getId(), classLogRequest);

        return ResponseEntity.ok(Result.of(classLogResponse));
    }
}
