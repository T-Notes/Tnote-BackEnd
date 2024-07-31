package com.example.tnote.boundedContext.proceeding.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/tnote/v1/proceeding")
@RequiredArgsConstructor
public class ProceedingController {
    private final ProceedingService proceedingService;

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> createProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long scheduleId,
                                                   @RequestPart ProceedingRequestDto proceedingRequestDto,
                                                   @RequestPart(name = "proceedingImages", required = false) List<MultipartFile> proceedingImages) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }
        ProceedingResponseDto proceedingResponseDto = proceedingService.save(principalDetails.getId(), scheduleId,
                proceedingRequestDto,
                proceedingImages);
        return ResponseEntity.ok(Result.of(proceedingResponseDto));
    }

    @GetMapping("/{scheduleId}/proceedings")
    public ResponseEntity<Result> getAllProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long scheduleId,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        ProceedingSliceResponseDto responseDto = proceedingService.readAllProceeding(principalDetails.getId(),
                scheduleId,
                pageRequest);

        return ResponseEntity.ok(Result.of(responseDto));
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

    @PatchMapping(value = "/{proceedingId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> updateProceeding(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long proceedingId,
                                                   @RequestPart ProceedingUpdateRequestDto updateRequestDto,
                                                   @RequestPart(name = "proceedingImages", required = false) List<MultipartFile> proceedingImages) {
        ProceedingResponseDto proceedingResponseDto = proceedingService.updateProceeding(principalDetails.getId(),
                proceedingId, updateRequestDto, proceedingImages);

        return ResponseEntity.ok(Result.of(proceedingResponseDto));
    }
}
