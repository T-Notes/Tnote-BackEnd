package com.example.tnote.boundedContext.proceeding.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSaveRequest;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequest;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
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
public class ProceedingController {
    private final ProceedingService proceedingService;

    public ProceedingController(final ProceedingService proceedingService) {
        this.proceedingService = proceedingService;
    }

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> save(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long scheduleId,
                                       @RequestPart final ProceedingSaveRequest request,
                                       @RequestPart(name = "proceedingImages", required = false) final List<MultipartFile> proceedingImages) {

        return ResponseEntity.ok(Result.of(proceedingService.save(principalDetails.getId(), scheduleId,
                request, proceedingImages)));
    }

    @GetMapping("/{scheduleId}/all")
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                          @PathVariable final Long scheduleId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(Result.of(proceedingService.findAll(principalDetails.getId(),
                scheduleId, pageRequest)));
    }

    @DeleteMapping("/{proceedingId}")
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long proceedingId) {

        return ResponseEntity.ok(
                (Result.of(proceedingService.delete(principalDetails.getId(), proceedingId))));
    }

    @GetMapping("/{proceedingId}")
    public ResponseEntity<Result> find(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long proceedingId) {

        return ResponseEntity.ok(Result.of(proceedingService.find(principalDetails.getId(), proceedingId)));
    }

    @PatchMapping(value = "/{proceedingId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> update(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long proceedingId,
                                         @RequestPart final ProceedingUpdateRequest request,
                                         @RequestPart(name = "proceedingImages", required = false) final List<MultipartFile> proceedingImages) {

        return ResponseEntity.ok(
                Result.of(proceedingService.update(principalDetails.getId(), proceedingId, request, proceedingImages)));
    }
}
