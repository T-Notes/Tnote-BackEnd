package com.example.tnote.boundedContext.observation.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ObservationService {
    private final ObservationRepository observationRepository;
    private final UserRepository userRepository;

    public ObservationResponseDto save(Long userId, ObservationRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        LocalDateTime startDate = DateUtils.adjustStartDateTime(requestDto.getStartDate(), requestDto.isAllDay());
        LocalDateTime endDate = DateUtils.adjustEndDateTime(requestDto.getEndDate(), requestDto.isAllDay());

        Observation observation = Observation.builder()
                .studentName(requestDto.getStudentName())
                .startDate(startDate)
                .endDate(endDate)
                .observationContents(requestDto.getObservationContents())
                .guidance(requestDto.getGuidance())
                .build();

        return ObservationResponseDto.of(observation);
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> readAllObservation(Long userId) {
        //todo slice 형태로 바꿔야합니다
        List<Observation> observations = observationRepository.findAllByUserId(userId);

        return observations.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public ObservationDetailResponseDto readObservationDetail(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId).orElseThrow();
        return new ObservationDetailResponseDto(observation);
    }

    public ObservationDeleteResponseDto deleteObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId).orElseThrow();
        observationRepository.delete(observation);

        return ObservationDeleteResponseDto.builder()
                .id(observation.getId())
                .build();
    }
}
