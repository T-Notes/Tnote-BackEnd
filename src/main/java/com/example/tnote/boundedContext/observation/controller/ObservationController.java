package com.example.tnote.boundedContext.observation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequestDto;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/tnote/v1/observation")
@RequiredArgsConstructor
public class ObservationController {
    private final ObservationService observationService;

    @PostMapping(value = "/{scheduleId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> createObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long scheduleId,
                                                    @RequestPart ObservationRequestDto observationRequestDto,
                                                    @RequestPart(name = "observationImages", required = false) List<MultipartFile> observationImages) {
        ObservationResponseDto observationResponseDto = observationService.save(principalDetails.getId(), scheduleId,
                observationRequestDto,
                observationImages);
        return ResponseEntity.ok(Result.of(observationResponseDto));
    }

    @GetMapping("/{scheduleId}/all")
    public ResponseEntity<Result> getAllObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long scheduleId,
                                                    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                    @RequestParam(value = "size", required = false, defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        ObservationSliceResponseDto responseDto = observationService.readAllObservation(principalDetails.getId(),
                scheduleId,
                pageRequest);

        return ResponseEntity.ok(Result.of(responseDto));
    }

    @GetMapping("/{observationId}")
    public ResponseEntity<Result> getObservationDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                       @PathVariable Long observationId) {
        ObservationDetailResponseDto responseDto = observationService.readObservationDetail(principalDetails.getId(),
                observationId);
        return ResponseEntity.ok(Result.of(responseDto));
    }

    @DeleteMapping("/{observationId}")
    public ResponseEntity<Result> deleteObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long observationId) {
        ObservationDeleteResponseDto responseDto = observationService.deleteObservation(principalDetails.getId(),
                observationId);
        return ResponseEntity.ok(Result.of(responseDto));
    }

    @PatchMapping(value = "/{observationId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> updateObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long observationId,
                                                    @RequestPart ObservationUpdateRequestDto observationUpdateRequestDto,
                                                    @RequestPart(name = "observationImages", required = false) List<MultipartFile> observationImages) {
        ObservationResponseDto responseDto = observationService.updateObservation(principalDetails.getId(),
                observationId, observationUpdateRequestDto, observationImages);
        return ResponseEntity.ok(Result.of(responseDto));
    }
}
