package com.example.tnote.boundedContext.observation.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequestDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ObservationService {
    private final ObservationRepository observationRepository;
    private final ObservationImageRepository observationImageRepository;
    private final UserRepository userRepository;

    public ObservationResponseDto save(Long userId, ObservationRequestDto requestDto,
                                       List<MultipartFile> observationImages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Observation observation = requestDto.toEntity(user);
        if (observationImages != null && !observationImages.isEmpty()) {
            List<ObservationImage> uploadedImages = uploadObservationImages(observation, observationImages);
            observation.getObservationImage().addAll(uploadedImages); // 이미지 리스트에 추가
        }
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
        List<ObservationImage> observationImages = observationImageRepository.findObservationImageById(observationId);
        return new ObservationDetailResponseDto(observation, observationImages);
    }

    public ObservationDeleteResponseDto deleteObservation(Long userId, Long observationId) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId).orElseThrow();
        observationRepository.delete(observation);

        return ObservationDeleteResponseDto.builder()
                .id(observation.getId())
                .build();
    }

    public ObservationResponseDto updateObservation(Long userId, Long observationId,
                                                    ObservationUpdateRequestDto requestDto) {
        Observation observation = observationRepository.findByIdAndUserId(observationId, userId).orElseThrow();
        updateEachItem(observation, requestDto);
        return ObservationResponseDto.of(observation);
    }

    private void updateEachItem(Observation observation, ObservationUpdateRequestDto requestDto) {
        if (requestDto.hasObservationContents()) {
            observation.updateObservationContents(requestDto.getObservationContents());
        }
        if (requestDto.hasGuidance()) {
            observation.updateGuidance(requestDto.getGuidance());
        }
    }

    private List<ObservationImage> uploadObservationImages(Observation observation,
                                                           List<MultipartFile> observationImages) {
        return observationImages.stream()
                .map(file -> createObservationImage(observation, file))
                .toList();
    }

    private ObservationImage createObservationImage(Observation observation, MultipartFile file) {
        String url;
        try {
            url = FileUploadUtils.saveFileAndGetUrl(file);
        } catch (IOException e) {
            log.error("File upload fail", e);
            throw new IllegalArgumentException();
        }

        log.info("url = {}", url);
        observation.clearObservationImages();

        return observationImageRepository.save(ObservationImage.builder()
                .observationImageUrl(url)
                .observation(observation)
                .build());
    }

    public List<ObservationResponseDto> readDailyObservations(Long userId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Observation> classLogs = observationRepository.findByUserIdAndStartDateBetween(userId, startOfDay,
                endOfDay);
        return classLogs.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }
}
