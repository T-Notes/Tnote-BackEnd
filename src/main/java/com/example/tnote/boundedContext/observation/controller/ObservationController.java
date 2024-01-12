package com.example.tnote.boundedContext.observation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<Result> getAllObservations(@AuthenticationPrincipal PrincipalDetails principalDetails){
        List<ObservationResponseDto> observations = observationService.readAllClassLog(principalDetails.getId());

        return ResponseEntity.ok(Result.of(observations));
    }
}
