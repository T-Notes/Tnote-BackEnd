package com.example.tnote.boundedContext.observation.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequestDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.exception.ObservationErrorCode;
import com.example.tnote.boundedContext.observation.exception.ObservationException;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.exception.UserErrorCode;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ObservationResponseDto save(Long userId, Long scheduleId, ObservationRequestDto requestDto,
                                       List<MultipartFile> observationImages) {
        User user = findUserById(userId);
        Schedule schedule = findScheduleById(scheduleId);
        Observation observation = observationRepository.save(requestDto.toEntity(user, schedule));

        if (observation.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || observation.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ObservationException(ObservationErrorCode.INVALID_OBSERVATION_DATE);
        }
        if (observationImages != null && !observationImages.isEmpty()) {
            List<ObservationImage> uploadedImages = uploadObservationImages(observation, observationImages);
            observation.getObservationImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, observation.getId(), scheduleId, "OBSERVATION");
        return ObservationResponseDto.of(observation);
    }

    @Transactional(readOnly = true)
    public ObservationSliceResponseDto readAllObservation(Long userId, Long scheduleId, Pageable pageable) {
        List<Observation> observations = observationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Observation> allObservationSlice = observationRepository.findAllByScheduleId(scheduleId, pageable);
        List<ObservationResponseDto> responseDto = allObservationSlice.getContent().stream()
                .map(ObservationResponseDto::of).toList();

        return ObservationSliceResponseDto.from(responseDto, observations, allObservationSlice);
    }

    public ObservationDetailResponseDto readObservationDetail(Long userId, Long observationId) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);
        List<ObservationImage> observationImages = observationImageRepository.findObservationImageByObservationId(
                observationId);
        recentLogService.saveRecentLog(userId, observation.getId(), observation.getSchedule().getId(), "OBSERVATION");
        return new ObservationDetailResponseDto(observation, observationImages);
    }

    public ObservationDeleteResponseDto deleteObservation(Long userId, Long observationId) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);

        deleteExistedImagesByObservation(observation);
        observationRepository.delete(observation);
        recentLogService.deleteRecentLog(observation.getId(), "OBSERVATION");

        return ObservationDeleteResponseDto.of(observation.getId());
    }

    public int deleteObservations(Long userId, List<Long> observationIds) {
        observationIds.forEach(observationId -> {
            deleteObservation(userId, observationId);
        });
        return observationIds.size();
    }

    public ObservationResponseDto updateObservation(Long userId, Long observationId,
                                                    ObservationUpdateRequestDto requestDto,
                                                    List<MultipartFile> observationImages) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);
        updateObservationItem(requestDto, observation, observationImages);
        recentLogService.saveRecentLog(userId, observation.getId(), observation.getSchedule().getId(), "OBSERVATION");
        return ObservationResponseDto.of(observation);
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findLogsByScheduleAndUser(Long scheduleId, Long userId) {
        List<Observation> logs = observationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findByTitle(String keyword, LocalDate startDate,
                                                    LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findByContents(String keyword, LocalDate startDate,
                                                       LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findByTitleOrPlanOrContents(String keyword,
                                                                    LocalDate startDate,
                                                                    LocalDate endDate,
                                                                    Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    private void updateObservationItem(ObservationUpdateRequestDto requestDto, Observation observation,
                                       List<MultipartFile> observationImages) {
        updateObservationFields(requestDto, observation);
        if (observationImages == null || observationImages.isEmpty()) {
            deleteExistedImages(observation);
        }
        if (observationImages != null && !observationImages.isEmpty()) {
            List<ObservationImage> updatedImages = deleteExistedImagesAndUploadNewImages(observation,
                    observationImages);
            observation.updateObservationImage(updatedImages);
        }
    }

    private void updateObservationFields(ObservationUpdateRequestDto requestDto, Observation observation) {
        observation.updateStudentName(requestDto.getTitle());
        observation.updateStartDate(requestDto.getStartDate());
        observation.updateEndDate(requestDto.getEndDate());
        observation.updateObservationContents(requestDto.getObservationContents());
        observation.updateGuidance(requestDto.getGuidance());
    }

    private List<ObservationImage> uploadObservationImages(Observation observation,
                                                           List<MultipartFile> observationImages) {
        return observationImages.stream()
                .map(file -> awsS3Uploader.upload(file, "observation"))
                .map(pair -> createObservationImage(observation, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ObservationImage createObservationImage(Observation observation, String url, String originalFileName) {
        log.info("url = {}", url);
        observation.clearObservationImages();

        return observationImageRepository.save(ObservationImage.builder()
                .observationImageUrl(url)
                .observation(observation)
                .originalFileName(originalFileName)
                .build());
    }

    @Transactional(readOnly = true)
    public ObservationSliceResponseDto readObservationsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                              LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Observation> allObservationSlice = observationRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        List<ObservationResponseDto> responseDto = allObservationSlice.getContent().stream()
                .map(ObservationResponseDto::of).toList();

        return ObservationSliceResponseDto.from(responseDto, observations, allObservationSlice);
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> readDailyObservations(Long userId, Long scheduleId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return observations.stream()
                .map(ObservationResponseDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> readMonthlyObservations(Long userId, Long scheduleId, LocalDate date) {
        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return observations.stream()
                .map(ObservationResponseDto::of).toList();
    }

    private List<ObservationImage> deleteExistedImagesAndUploadNewImages(Observation observation,
                                                                         List<MultipartFile> observationImages) {
        deleteExistedImages(observation);
        return uploadObservationImages(observation, observationImages);
    }

    private void deleteExistedImages(Observation observation) {
        deleteS3Images(observation);
        observationImageRepository.deleteByObservationId(observation.getId());
    }

    private void deleteExistedImagesByObservation(Observation observation) {
        deleteS3Images(observation);
    }

    private void deleteS3Images(Observation observation) {
        List<ObservationImage> observationImages = observation.getObservationImage();
        for (ObservationImage observationImage : observationImages) {
            String imageKey = observationImage.getObservationImageUrl().substring(49);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    private Observation findObservationByIdAndUserId(Long observationId, Long userId) {
        return observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new ObservationException(ObservationErrorCode.OBSERVATION_NOT_FOUNT));
    }
}
