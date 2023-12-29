package com.example.tnote.boundedContext.classLog.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
                                                 @RequestBody ClassLogRequestDto classLogRequestDto) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }
        Long userId = principalDetails.getId();
        ClassLogResponseDto classLogResponseDto = classLogService.save(userId, classLogRequestDto);

        return ResponseEntity.ok(Result.of(classLogResponseDto));
    }

    @GetMapping("/classLogs")
    public ResponseEntity<Result> getAllClassLogs(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ClassLog> classLogs = classLogService.readAllClassLog(principalDetails.getId());

        List<ClassLogResponseDto> classLogDtos = classLogs.stream()
                .map(ClassLogResponseDto::of)
                .toList();

        return ResponseEntity.ok(Result.of(classLogDtos));
    }

    @DeleteMapping("/{classLogId}")
    public ResponseEntity<Result> deleteClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable Long classLogId) {
        ClassLogDeleteResponseDto deleteResponseDto = classLogService.deleteClassLog(principalDetails.getId(), classLogId);
        return ResponseEntity.ok(Result.of(deleteResponseDto));
    }
}
