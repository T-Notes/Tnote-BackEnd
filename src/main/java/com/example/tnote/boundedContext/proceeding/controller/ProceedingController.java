package com.example.tnote.boundedContext.proceeding.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
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
@RequestMapping("/proceeding")
@RequiredArgsConstructor
public class ProceedingController {
    private final ProceedingService proceedingService;

    @PostMapping("/proceedings")
    public ResponseEntity<Result> createProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @RequestBody
                                                   ProceedingRequestDto requestDto) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }
        ProceedingResponseDto proceedingResponseDto = proceedingService.save(principalDetails.getId(), requestDto);
        return ResponseEntity.ok(Result.of(proceedingResponseDto));

    }
}
