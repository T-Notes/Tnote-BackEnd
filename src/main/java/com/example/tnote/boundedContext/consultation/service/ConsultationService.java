package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSaveRequest;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponses;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequest;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.exception.ConsultationErrorCode;
import com.example.tnote.boundedContext.consultation.exception.ConsultationException;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
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
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationImageRepository consultationImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ConsultationService(final ConsultationRepository consultationRepository,
                               final ConsultationImageRepository consultationImageRepository,
                               final UserRepository userRepository,
                               final ScheduleRepository scheduleRepository, final RecentLogService recentLogService,
                               final AwsS3Uploader awsS3Uploader) {
        this.consultationRepository = consultationRepository;
        this.consultationImageRepository = consultationImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.recentLogService = recentLogService;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public ConsultationResponse save(final Long userId, final Long scheduleId, final ConsultationSaveRequest request,
                                     final List<MultipartFile> consultationImages) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        Consultation consultation = consultationRepository.save(request.toEntity(user, schedule));

        validateIncorrectTime(request, schedule);
        validateHasImages(consultation, consultationImages);

        recentLogService.save(userId, consultation.getId(), scheduleId, "CONSULTATION");
        return ConsultationResponse.from(consultation);
    }

    public ConsultationResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<Consultation> consultations = consultationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Consultation> allConsultations = consultationRepository.findAllByScheduleId(scheduleId, pageable);
        List<ConsultationResponse> responses = allConsultations.getContent().stream()
                .map(ConsultationResponse::from).toList();

        return ConsultationResponses.of(responses, consultations, allConsultations);
    }

    @Transactional
    public ConsultationDeleteResponse delete(final Long userId, final Long consultationId) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);

        deleteExistedImagesByConsultation(consultation);
        consultationRepository.delete(consultation);
        recentLogService.delete(consultation.getId(), "CONSULTATION");

        return ConsultationDeleteResponse.from(consultation);
    }

    @Transactional
    public int deleteConsultations(final Long userId, final List<Long> consultationIds) {
        consultationIds.forEach(consultationId -> {
            delete(userId, consultationId);
        });
        return consultationIds.size();
    }

    @Transactional
    public ConsultationResponse find(final Long userId, final Long consultationId) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        recentLogService.save(userId, consultation.getId(), consultation.getSchedule().getId(), "CONSULTATION");
        return ConsultationResponse.from(consultation);
    }

    @Transactional
    public ConsultationResponse update(final Long userId, final Long consultationId,
                                       final ConsultationUpdateRequest request,
                                       final List<MultipartFile> consultationImages) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        updateEachItem(request, consultation, consultationImages);
        recentLogService.save(userId, consultation.getId(), consultation.getSchedule().getId(), "CONSULTATION");

        return ConsultationResponse.from(consultation);
    }

    public List<ConsultationResponse> findByScheduleAndUser(final Long scheduleId, final Long userId) {
        List<Consultation> logs = consultationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    public List<ConsultationResponse> findByFilter(final Long userId, final LocalDate startDate,
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

    private List<ConsultationResponse> findByTitle(final String keyword, final LocalDate startDate,
                                                   final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private List<ConsultationResponse> findByContents(final String keyword, final LocalDate startDate,
                                                      final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private List<ConsultationResponse> findByTitleOrPlanOrContents(final String keyword,
                                                                   final LocalDate startDate,
                                                                   final LocalDate endDate,
                                                                   final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private void updateEachItem(final ConsultationUpdateRequest request, final Consultation consultation,
                                final List<MultipartFile> consultationImages) {
        updateFields(request, consultation);
        if (consultationImages == null || consultationImages.isEmpty()) {
            deleteExistedImages(consultation);
        }
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> updatedImages = deleteExistedImagesAndUploadNewImages(consultation,
                    consultationImages);
            consultation.updateConsultationImages(updatedImages);
        }
    }

    private void updateFields(final ConsultationUpdateRequest request, final Consultation consultation) {
        consultation.updateStudentName(request.getTitle());
        consultation.updateStartDate(request.getStartDate());
        consultation.updateEndDate(request.getEndDate());
        consultation.updateConsultationContents(request.getConsultationContents());
        consultation.updateConsultationResult(request.getConsultationResult());
        request.validateEnums();
        consultation.updateCounselingField(request.getCounselingField());
        request.validateEnums();
        consultation.updateCounselingType(request.getCounselingType());
    }

    private List<ConsultationImage> uploadImages(final Consultation consultation,
                                                 final List<MultipartFile> consultationImages) {
        return consultationImages.stream()
                .map(file -> awsS3Uploader.upload(file, "consultation"))
                .map(pair -> createImage(consultation, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ConsultationImage createImage(final Consultation consultation, final String url,
                                          final String originalFileName) {
        consultation.clearConsultationImages();

        return consultationImageRepository.save(ConsultationImage.builder()
                .consultationImageUrl(url)
                .consultation(consultation)
                .originalFileName(originalFileName)
                .build());
    }

    public ConsultationResponses readConsultationsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                         LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Consultation> allConsultations = consultationRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        List<ConsultationResponse> responseDtos = allConsultations.getContent().stream()
                .map(ConsultationResponse::from).toList();

        return ConsultationResponses.of(responseDtos, consultations, allConsultations);
    }

    public List<ConsultationResponse> findDaily(final Long userId, final Long scheduleId, final LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return consultations.stream()
                .map(ConsultationResponse::from).toList();
    }

    public List<ConsultationResponse> findMonthly(final Long userId, final Long scheduleId, final LocalDate date) {
        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return consultations.stream()
                .map(ConsultationResponse::from).toList();
    }

    private List<ConsultationImage> deleteExistedImagesAndUploadNewImages(final Consultation consultation,
                                                                          final List<MultipartFile> consultationImages) {
        deleteExistedImages(consultation);
        return uploadImages(consultation, consultationImages);
    }

    private void deleteExistedImages(final Consultation consultation) {
        deleteS3Images(consultation);
        consultationImageRepository.deleteByConsultationId(consultation.getId());
    }

    private void deleteExistedImagesByConsultation(final Consultation consultation) {
        deleteS3Images(consultation);
    }

    private void deleteS3Images(final Consultation consultation) {
        List<ConsultationImage> consultationImages = consultation.getConsultationImage();
        for (ConsultationImage consultationImage : consultationImages) {
            String imageKey = consultationImage.getConsultationImageUrl().substring(49);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private Consultation findConsultationByIdAndUserId(final Long consultationId, final Long userId) {
        return consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(ConsultationErrorCode.CONSULTATION_NOT_FOUNT));
    }

    private void validateIncorrectTime(final ConsultationSaveRequest request, final Schedule schedule) {
        if (request.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || request.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ConsultationException(ConsultationErrorCode.INVALID_CONSULTATION_DATE);
        }
    }

    private void validateHasImages(final Consultation consultation, final List<MultipartFile> consultationImages) {
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> uploadedImages = uploadImages(consultation, consultationImages);
            consultation.getConsultationImage().addAll(uploadedImages);
        }
    }
}
