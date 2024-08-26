package com.example.tnote.boundedContext.observation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.observation.dto.ObservationSaveRequest;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequest;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
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
@RequestMapping("/tnote/v1/observation")
public class ObservationController {
    private final ObservationService observationService;

    public ObservationController(final ObservationService observationService) {
        this.observationService = observationService;
    }

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> save(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long scheduleId,
                                       @RequestPart final ObservationSaveRequest request,
                                       @RequestPart(name = "observationImages", required = false) final List<MultipartFile> observationImages) {

        return ResponseEntity.ok(Result.of(observationService.save(principalDetails.getId(), scheduleId,
                request, observationImages)));
    }

    @GetMapping("/{scheduleId}/all")
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                          @PathVariable final Long scheduleId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(Result.of(observationService.findAll(principalDetails.getId(),
                scheduleId, pageRequest)));
    }

    @GetMapping("/{observationId}")
    public ResponseEntity<Result> find(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @PathVariable final Long observationId) {

        return ResponseEntity.ok(Result.of(observationService.find(principalDetails.getId(),
                observationId)));
    }

    @DeleteMapping("/{observationId}")
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long observationId) {

        return ResponseEntity.ok(Result.of(observationService.delete(principalDetails.getId(), observationId)));
    }

    @PatchMapping(value = "/{observationId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> update(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @PathVariable final Long observationId,
                                         @RequestPart final ObservationUpdateRequest request,
                                         @RequestPart(name = "observationImages", required = false) final List<MultipartFile> observationImages) {

        return ResponseEntity.ok(Result.of(observationService.update(principalDetails.getId(),
                observationId, request, observationImages)));
    }
}
