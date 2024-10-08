package com.example.tnote.boundedContext.observation.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationSaveRequest;
import com.example.tnote.boundedContext.observation.dto.ObservationResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationResponses;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequest;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.exception.ObservationErrorCode;
import com.example.tnote.boundedContext.observation.exception.ObservationException;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@Service
public class ObservationService {
    private final ObservationRepository observationRepository;
    private final ObservationImageRepository observationImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ObservationService(final ObservationRepository observationRepository,
                              final ObservationImageRepository observationImageRepository,
                              final UserRepository userRepository,
                              final ScheduleRepository scheduleRepository, final RecentLogService recentLogService,
                              final AwsS3Uploader awsS3Uploader) {
        this.observationRepository = observationRepository;
        this.observationImageRepository = observationImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.recentLogService = recentLogService;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public ObservationResponse save(final Long userId, final Long scheduleId, final ObservationSaveRequest request,
                                    final List<MultipartFile> observationImages) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        Observation observation = request.toEntity(user, schedule);

        validateIncorrectTime(request, schedule);
        observation = observationRepository.save(observation);

        if (observationImages != null && !observationImages.isEmpty()) {
            List<ObservationImage> uploadedImages = uploadObservationImages(observation, observationImages);
            observation.getObservationImage().addAll(uploadedImages);
        }
        recentLogService.save(userId, observation.getId(), scheduleId, "OBSERVATION");
        return ObservationResponse.from(observation);
    }

    public ObservationResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<Observation> observations = observationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Observation> allObservationSlice = observationRepository.findAllByScheduleId(scheduleId, pageable);
        List<ObservationResponse> responseDto = allObservationSlice.getContent().stream()
                .map(ObservationResponse::from).toList();

        return ObservationResponses.of(responseDto, observations, allObservationSlice);
    }

    @Transactional
    public ObservationResponse find(final Long userId, final Long observationId) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);
        recentLogService.save(userId, observation.getId(), observation.getSchedule().getId(), "OBSERVATION");
        return ObservationResponse.from(observation);
    }

    @Transactional
    public ObservationDeleteResponse delete(final Long userId, final Long observationId) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);

        deleteExistedImagesByObservation(observation);
        observationRepository.delete(observation);
        recentLogService.delete(observation.getId(), "OBSERVATION");

        return ObservationDeleteResponse.from(observation.getId());
    }

    @Transactional
    public int deleteObservations(final Long userId, final List<Long> observationIds) {
        observationIds.forEach(observationId -> {
            delete(userId, observationId);
        });
        return observationIds.size();
    }

    @Transactional
    public ObservationResponse update(final Long userId, final Long observationId,
                                      final ObservationUpdateRequest request,
                                      final List<MultipartFile> observationImages) {
        Observation observation = findObservationByIdAndUserId(observationId, userId);
        updateObservationItem(request, observation, observationImages);
        recentLogService.save(userId, observation.getId(), observation.getSchedule().getId(), "OBSERVATION");
        return ObservationResponse.from(observation);
    }

    public List<ObservationResponse> findByScheduleAndUser(Long scheduleId, Long userId) {
        List<Observation> logs = observationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    public List<ObservationResponse> findByFilter(final Long userId, final LocalDate startDate,
                                                  final LocalDate endDate,
                                                  final String searchType, final String keyword) {
        if ("title".equals(searchType)) {
            return findByTitle(keyword, startDate, endDate, userId);
        }
        if ("content".equals((searchType))) {
            return findByContents(keyword, startDate, endDate, userId);
        }
        if ("titleAndContent".equals(searchType)) {
            return findByTitleOrPlanOrContents(keyword, startDate, endDate, userId);
        }
        return null;
    }

    private List<ObservationResponse> findByTitle(final String keyword, final LocalDate startDate,
                                                  final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    private List<ObservationResponse> findByContents(final String keyword, final LocalDate startDate,
                                                     final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    private List<ObservationResponse> findByTitleOrPlanOrContents(final String keyword,
                                                                  final LocalDate startDate,
                                                                  final LocalDate endDate,
                                                                  final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Observation> logs = observationRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    private void updateObservationItem(final ObservationUpdateRequest request, final Observation observation,
                                       final List<MultipartFile> observationImages) {
        updateObservationFields(request, observation);
        if (observationImages == null || observationImages.isEmpty()) {
            deleteExistedImages(observation);
        }
        if (observationImages != null && !observationImages.isEmpty()) {
            List<ObservationImage> updatedImages = deleteExistedImagesAndUploadNewImages(observation,
                    observationImages);
            observation.updateObservationImage(updatedImages);
        }
    }

    private void updateObservationFields(final ObservationUpdateRequest request, final Observation observation) {
        observation.updateStudentName(request.getTitle());
        observation.updateStartDate(request.getStartDate());
        observation.updateEndDate(request.getEndDate());
        observation.updateObservationContents(request.getObservationContents());
        observation.updateGuidance(request.getGuidance());
    }

    private List<ObservationImage> uploadObservationImages(final Observation observation,
                                                           final List<MultipartFile> observationImages) {
        return observationImages.stream()
                .map(file -> awsS3Uploader.upload(file, "observation"))
                .map(pair -> createObservationImage(observation, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ObservationImage createObservationImage(final Observation observation, final String url,
                                                    final String originalFileName) {
        observation.clearObservationImages();

        return observationImageRepository.save(ObservationImage.builder()
                .observationImageUrl(url)
                .observation(observation)
                .originalFileName(originalFileName)
                .build());
    }

    @Transactional(readOnly = true)
    public ObservationResponses readObservationsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                       LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Observation> allObservationSlice = observationRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        List<ObservationResponse> responseDto = allObservationSlice.getContent().stream()
                .map(ObservationResponse::from).toList();

        return ObservationResponses.of(responseDto, observations, allObservationSlice);
    }

    public List<ObservationResponse> findDaily(final Long userId, final Long scheduleId, final LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return observations.stream()
                .map(ObservationResponse::from).toList();
    }

    public List<ObservationResponse> findMonthly(final Long userId, final Long scheduleId, final LocalDate date) {
        List<Observation> observations = observationRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return observations.stream()
                .map(ObservationResponse::from).toList();
    }

    private List<ObservationImage> deleteExistedImagesAndUploadNewImages(final Observation observation,
                                                                         final List<MultipartFile> observationImages) {
        deleteExistedImages(observation);
        return uploadObservationImages(observation, observationImages);
    }

    private void deleteExistedImages(final Observation observation) {
        deleteS3Images(observation);
        observationImageRepository.deleteByObservationId(observation.getId());
    }

    private void deleteExistedImagesByObservation(final Observation observation) {
        deleteS3Images(observation);
    }

    private void deleteS3Images(final Observation observation) {
        List<ObservationImage> observationImages = observation.getObservationImage();
        for (ObservationImage observationImage : observationImages) {
            String imageKey = observationImage.getObservationImageUrl().substring(49);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private Observation findObservationByIdAndUserId(final Long observationId, final Long userId) {
        return observationRepository.findByIdAndUserId(observationId, userId)
                .orElseThrow(() -> new ObservationException(ObservationErrorCode.OBSERVATION_NOT_FOUNT));
    }

    private void validateIncorrectTime(final ObservationSaveRequest request, final Schedule schedule) {
        if (request.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || request.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ObservationException(ObservationErrorCode.INVALID_OBSERVATION_DATE);
        }
    }
}
