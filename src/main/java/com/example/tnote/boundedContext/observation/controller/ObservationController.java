package com.example.tnote.boundedContext.observation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequestDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/observation")
@RequiredArgsConstructor
public class ObservationController {
    private final ObservationService observationService;

    @PostMapping("/observations")
    public ResponseEntity<Result> createObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @RequestBody
                                                    ObservationRequestDto requestDto) {
        ObservationResponseDto observationResponseDto = observationService.save(principalDetails.getId(), requestDto);
        return ResponseEntity.ok(Result.of(observationResponseDto));
    }

    @GetMapping("/observations")
    public ResponseEntity<Result> getAllObservations(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ObservationResponseDto> observations = observationService.readAllObservation(principalDetails.getId());

        return ResponseEntity.ok(Result.of(observations));
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

    @PatchMapping("/{observationId}")
    public ResponseEntity<Result> updateObservation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long observationId,
                                                    @RequestBody ObservationUpdateRequestDto requestDto) {
        ObservationResponseDto responseDto = observationService.updateObservation(principalDetails.getId(), observationId, requestDto);
        return ResponseEntity.ok(Result.of(responseDto));
    }
}
