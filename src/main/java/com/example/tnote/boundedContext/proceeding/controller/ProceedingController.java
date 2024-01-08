package com.example.tnote.boundedContext.proceeding.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
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

    @GetMapping("/proceedings")
    public ResponseEntity<Result> getAllProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        //todo slice 형태로 바꿔야합니다.
        List<ProceedingResponseDto> proceedingList = proceedingService.readAllProceeding(principalDetails.getId());

        return ResponseEntity.ok(Result.of(proceedingList));
    }

    @DeleteMapping("/{proceedingId}")
    public ResponseEntity<Result> deleteProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long proceedingId) {
        ProceedingDeleteResponseDto proceedingDeleteResponseDto = proceedingService.deleteProceeding(
                principalDetails.getId(), proceedingId);

        return ResponseEntity.ok((Result.of(proceedingDeleteResponseDto)));
    }

    @GetMapping("/{proceedingId}")
    public ResponseEntity<Result> getProceedingDetails(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable Long proceedingId) {
        //todo 아카이브 컨트롤러로 위치 변경해야하는지 회의필요
        ProceedingDetailResponseDto proceedingDetailResponseDto = proceedingService.getProceedingDetails(
                principalDetails.getId(), proceedingId);

        return ResponseEntity.ok(Result.of(proceedingDetailResponseDto));
    }
}
