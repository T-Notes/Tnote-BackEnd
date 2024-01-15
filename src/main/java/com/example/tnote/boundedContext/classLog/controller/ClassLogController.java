package com.example.tnote.boundedContext.classLog.controller;

import static com.example.tnote.base.exception.CommonErrorResult.UNAUTHORIZED;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/classLog")
@RequiredArgsConstructor
public class ClassLogController {
    private final ClassLogService classLogService;

    @PostMapping(value = "/classLogs", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> createClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @RequestPart ClassLogRequestDto classLogRequestDto,
                                                 @RequestPart(name = "classLogImages", required = false) List<MultipartFile> classLogImages) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of(UNAUTHORIZED.getMessage()));
        }
        ClassLogResponseDto classLogResponseDto = classLogService.save(principalDetails.getId(), classLogRequestDto,
                classLogImages);

        return ResponseEntity.ok(Result.of(classLogResponseDto));
    }

    @GetMapping("/classLogs")
    public ResponseEntity<Result> getAllClassLogs(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ClassLogResponseDto> classLogDto = classLogService.readAllClassLog(principalDetails.getId());

        return ResponseEntity.ok(Result.of(classLogDto));
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
        //Todo 나중에 아카이브 컨트롤러로 빼야할수도 있습니다 회의가 필요합니다.
        ClassLogDetailResponseDto detailResponseDto = classLogService.getClassLogDetail(principalDetails.getId(),
                classLogId);
        return ResponseEntity.ok(Result.of(detailResponseDto));
    }

    @PatchMapping("/{classLogId}")
    public ResponseEntity<Result> updateClassLog(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                 @PathVariable Long classLogId, @RequestBody
                                                 ClassLogUpdateRequestDto classLogUpdateRequestDto) {
        ClassLogResponseDto classLogResponseDto = classLogService.updateClassLog(principalDetails.getId(), classLogId,
                classLogUpdateRequestDto);
        return ResponseEntity.ok(Result.of(classLogResponseDto));
    }

}
