package com.example.tnote.boundedContext.consultation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSaveRequest;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequest;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.Arrays;
import java.util.List;
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

@RestController
@RequestMapping("/tnote/v1/consultation")
public class ConsultationController {
    private final ConsultationService consultationService;

    public ConsultationController(final ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping("/field")
    public ResponseEntity<Result> findCounselingField() {
        return ResponseEntity.ok(Result.of(Arrays.asList(CounselingField.values())));
    }

    @GetMapping("/type")
    public ResponseEntity<Result> findCounselingType() {
        return ResponseEntity.ok(Result.of(Arrays.asList(CounselingType.values())));
    }

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> save(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long scheduleId,
                                       @RequestPart final ConsultationSaveRequest request,
                                       @RequestPart(name = "consultationImages", required = false) final List<MultipartFile> consultationImages) {

        return ResponseEntity.ok(Result.of(consultationService.save(principalDetails.getId(), scheduleId,
                request, consultationImages)));
    }

    @GetMapping("/{scheduleId}/all")
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                          @PathVariable final Long scheduleId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(Result.of(consultationService.findAll(principalDetails.getId(),
                scheduleId, pageRequest)));
    }

    @DeleteMapping("/{consultationId}")
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long consultationId) {

        return ResponseEntity.ok(Result.of(consultationService.delete(principalDetails.getId(), consultationId)));
    }

    @GetMapping("/{consultationId}")
    public ResponseEntity<Result> find(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long consultationId) {

        return ResponseEntity.ok(Result.of(consultationService.find(principalDetails.getId(), consultationId)));
    }

    @PatchMapping(value = "/{consultationId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> update(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long consultationId,
                                         @RequestPart final ConsultationUpdateRequest request,
                                         @RequestPart(name = "consultationImages", required = false) final List<MultipartFile> consultationImages) {

        return ResponseEntity.ok(Result.of(consultationService.update(principalDetails.getId(),
                consultationId, request, consultationImages)));
    }
}
