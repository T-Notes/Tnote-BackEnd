package com.example.tnote.boundedContext.classLog.controller;


import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
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
@RequestMapping("/tnote/v1/classLog")
@RequiredArgsConstructor
public class ClassLogController {
    private final ClassLogService classLogService;

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> createClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable Long scheduleId,
                                                 @RequestPart ClassLogRequestDto classLogRequestDto,
                                                 @RequestPart(name = "classLogImages", required = false) List<MultipartFile> classLogImages) {
        ClassLogResponseDto classLogResponseDto = classLogService.save(principalDetails.getId(), scheduleId,
                classLogRequestDto,
                classLogImages);

        return ResponseEntity.ok(Result.of(classLogResponseDto));
    }

    @GetMapping("/{scheduleId}/all")
    public ResponseEntity<Result> getAllClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @PathVariable Long scheduleId,
                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        ClassLogSliceResponseDto responseDto = classLogService.readAllClassLog(principalDetails.getId(), scheduleId,
                pageRequest);

        return ResponseEntity.ok(Result.of(responseDto));
    }

    @DeleteMapping("/{classLogId}")
    public ResponseEntity<Result> deleteClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable Long classLogId) {
        ClassLogDeleteResponseDto deleteResponseDto = classLogService.deleteClassLog(principalDetails.getId(),
                classLogId);
        return ResponseEntity.ok(Result.of(deleteResponseDto));
    }

    @GetMapping("/{classLogId}")
    public ResponseEntity<Result> getClassLogDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long classLogId) {
        ClassLogDetailResponseDto detailResponseDto = classLogService.getClassLogDetail(principalDetails.getId(),
                classLogId);
        return ResponseEntity.ok(Result.of(detailResponseDto));
    }

    @PatchMapping(value = "/{classLogId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> updateClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable Long classLogId,
                                                 @RequestPart ClassLogUpdateRequestDto classLogUpdateRequestDto,
                                                 @RequestPart(name = "classLogImages", required = false) List<MultipartFile> classLogImages) {
        ClassLogResponseDto classLogResponseDto = classLogService.updateClassLog(principalDetails.getId(), classLogId,
                classLogUpdateRequestDto, classLogImages);
        return ResponseEntity.ok(Result.of(classLogResponseDto));
    }

}
