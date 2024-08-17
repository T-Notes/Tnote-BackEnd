package com.example.tnote.boundedContext.classLog.controller;


import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSaveRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequest;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/tnote/v1/classLog")
public class ClassLogController {
    private final ClassLogService classLogService;

    public ClassLogController(final ClassLogService classLogService) {
        this.classLogService = classLogService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> save(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @RequestParam final Long scheduleId,
                                       @RequestPart final ClassLogSaveRequest classLogRequestDto,
                                       @RequestPart(name = "classLogImages", required = false) final List<MultipartFile> classLogImages) {

        return ResponseEntity.ok(Result.of(classLogService.save(principalDetails.getId(), scheduleId,
                classLogRequestDto,
                classLogImages)));
    }

    @GetMapping("/all")
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                          @RequestParam final Long scheduleId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(Result.of(classLogService.findAll(principalDetails.getId(), scheduleId,
                pageRequest)));
    }

    @DeleteMapping()
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @RequestParam final Long classLogId) {

        return ResponseEntity.ok(Result.of(classLogService.delete(principalDetails.getId(),
                classLogId)));
    }

    @GetMapping()
    public ResponseEntity<Result> find(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @RequestParam final Long classLogId) {

        return ResponseEntity.ok(Result.of(classLogService.find(principalDetails.getId(),
                classLogId)));
    }

    @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> update(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @RequestParam final Long classLogId,
                                         @RequestPart final ClassLogUpdateRequest classLogUpdateRequestDto,
                                         @RequestPart(name = "classLogImages", required = false) final List<MultipartFile> classLogImages) {

        return ResponseEntity.ok(Result.of(classLogService.update(principalDetails.getId(), classLogId,
                classLogUpdateRequestDto, classLogImages)));
    }

}
