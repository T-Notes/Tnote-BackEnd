package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSaveRequest;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponses;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
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
    public int deleteConsultations(Long userId, List<Long> consultationIds) {
        consultationIds.forEach(consultationId -> {
            delete(userId, consultationId);
        });
        return consultationIds.size();
    }

    @Transactional
    public ConsultationDetailResponseDto getConsultationDetail(Long userId, Long consultationId) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        List<ConsultationImage> consultationImages = consultationImageRepository.findConsultationImageByConsultationId(
                consultationId);
        recentLogService.save(userId, consultation.getId(), consultation.getSchedule().getId(),
                "CONSULTATION");
        return new ConsultationDetailResponseDto(consultation, consultationImages);
    }

    @Transactional
    public ConsultationResponse updateConsultation(Long userId, Long consultationId,
                                                   ConsultationUpdateRequestDto requestDto,
                                                   List<MultipartFile> consultationImages) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        updateConsultationItem(requestDto, consultation, consultationImages);
        recentLogService.save(userId, consultation.getId(), consultation.getSchedule().getId(),
                "CONSULTATION");
        return ConsultationResponse.from(consultation);
    }

    public List<ConsultationResponse> findByScheduleAndUser(Long scheduleId, Long userId) {
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

    private List<ConsultationResponse> findByTitle(String keyword, LocalDate startDate,
                                                   LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private List<ConsultationResponse> findByContents(String keyword, LocalDate startDate,
                                                      LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private List<ConsultationResponse> findByTitleOrPlanOrContents(String keyword,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate,
                                                                   Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    private void updateConsultationItem(ConsultationUpdateRequestDto requestDto, Consultation consultation,
                                        List<MultipartFile> consultationImages) {
        updateConsultationFields(requestDto, consultation);
        if (consultationImages == null || consultationImages.isEmpty()) {
            deleteExistedImages(consultation);
        }
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> updatedImages = deleteExistedImagesAndUploadNewImages(consultation,
                    consultationImages);
            consultation.updateConsultationImages(updatedImages);
        }
    }

    private void updateConsultationFields(ConsultationUpdateRequestDto requestDto, Consultation consultation) {
        consultation.updateStudentName(requestDto.getTitle());
        consultation.updateStartDate(requestDto.getStartDate());
        consultation.updateEndDate(requestDto.getEndDate());
        consultation.updateConsultationContents(requestDto.getConsultationContents());
        consultation.updateConsultationResult(requestDto.getConsultationResult());
        requestDto.validateEnums();
        consultation.updateCounselingField(requestDto.getCounselingField());
        requestDto.validateEnums();
        consultation.updateCounselingType(requestDto.getCounselingType());
    }

    private List<ConsultationImage> uploadConsultationImages(Consultation consultation,
                                                             List<MultipartFile> consultationImages) {
        return consultationImages.stream()
                .map(file -> awsS3Uploader.upload(file, "consultation"))
                .map(pair -> createConsultationImage(consultation, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ConsultationImage createConsultationImage(Consultation consultation, String url, String originalFileName) {
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

    public List<ConsultationResponse> readDailyConsultations(Long userId, Long scheduleId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return consultations.stream()
                .map(ConsultationResponse::from).toList();
    }

    public List<ConsultationResponse> readMonthlyConsultations(Long userId, Long scheduleId, LocalDate date) {
        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return consultations.stream()
                .map(ConsultationResponse::from).toList();
    }

    private List<ConsultationImage> deleteExistedImagesAndUploadNewImages(Consultation consultation,
                                                                          List<MultipartFile> consultationImages) {
        deleteExistedImages(consultation);
        return uploadConsultationImages(consultation, consultationImages);
    }

    private void deleteExistedImages(Consultation consultation) {
        deleteS3Images(consultation);
        consultationImageRepository.deleteByConsultationId(consultation.getId());
    }

    private void deleteExistedImagesByConsultation(Consultation consultation) {
        deleteS3Images(consultation);
    }

    private void deleteS3Images(Consultation consultation) {
        List<ConsultationImage> consultationImages = consultation.getConsultationImage();
        for (ConsultationImage consultationImage : consultationImages) {
            String imageKey = consultationImage.getConsultationImageUrl().substring(49);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private Consultation findConsultationByIdAndUserId(Long consultationId, Long userId) {
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
            List<ConsultationImage> uploadedImages = uploadConsultationImages(consultation, consultationImages);
            consultation.getConsultationImage().addAll(uploadedImages);
        }
    }
}
