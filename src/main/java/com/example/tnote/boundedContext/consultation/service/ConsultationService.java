package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.exception.ConsultationErrorCode;
import com.example.tnote.boundedContext.consultation.exception.ConsultationException;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
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
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationImageRepository consultationImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ConsultationResponseDto save(Long userId, Long scheduleId, ConsultationRequestDto requestDto,
                                        List<MultipartFile> consultationImages) {
        requestDto.validateEnums();
        User user = findUserById(userId);
        Schedule schedule = findScheduleById(scheduleId);

        Consultation consultation = consultationRepository.save(requestDto.toEntity(user, schedule));
        if (consultation.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || consultation.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ConsultationException(ConsultationErrorCode.INVALID_CONSULTATION_DATE);
        }
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> uploadedImages = uploadConsultationImages(consultation, consultationImages);
            consultation.getConsultationImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, consultation.getId(), scheduleId, "CONSULTATION");
        return ConsultationResponseDto.of(consultation);
    }

    @Transactional(readOnly = true)
    public ConsultationSliceResponseDto readAllConsultation(Long userId, Long scheduleId, Pageable pageable) {
        List<Consultation> consultations = consultationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Consultation> allConsultations = consultationRepository.findAllByScheduleId(scheduleId, pageable);
        int numberOfConsultation = consultations.size();
        List<ConsultationResponseDto> responseDtos = allConsultations.getContent().stream()
                .map(ConsultationResponseDto::of).toList();

        return ConsultationSliceResponseDto.builder()
                .consultations(responseDtos)
                .numberOfConsultation(numberOfConsultation)
                .page(allConsultations.getPageable().getPageNumber())
                .isLast(allConsultations.isLast())
                .build();
    }

    public ConsultationDeleteResponseDto deleteConsultation(Long userId, Long consultationId) {
        Consultation consultation = consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(ConsultationErrorCode.CONSULTATION_NOT_FOUNT));

        deleteExistedImagesByConsultation(consultation);
        consultationRepository.delete(consultation);
        recentLogService.deleteRecentLog(consultation.getId(), "CONSULTATION");

        return ConsultationDeleteResponseDto.builder()
                .id(consultation.getId())
                .build();
    }

    public int deleteConsultations(Long userId, List<Long> consultationIds) {
        consultationIds.forEach(consultationId -> {
            deleteConsultation(userId, consultationId);
        });
        return consultationIds.size();
    }

    public ConsultationDetailResponseDto getConsultationDetail(Long userId, Long consultationId) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        List<ConsultationImage> consultationImages = consultationImageRepository.findConsultationImageByConsultationId(
                consultationId);
        recentLogService.saveRecentLog(userId, consultation.getId(), consultation.getSchedule().getId(),
                "CONSULTATION");
        return new ConsultationDetailResponseDto(consultation, consultationImages);
    }

    public ConsultationResponseDto updateConsultation(Long userId, Long consultationId,
                                                      ConsultationUpdateRequestDto requestDto,
                                                      List<MultipartFile> consultationImages) {
        Consultation consultation = findConsultationByIdAndUserId(consultationId, userId);
        updateConsultationItem(requestDto, consultation, consultationImages);
        recentLogService.saveRecentLog(userId, consultation.getId(), consultation.getSchedule().getId(),
                "CONSULTATION");
        return ConsultationResponseDto.of(consultation);
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findLogsByScheduleAndUser(Long scheduleId, Long userId) {
        List<Consultation> logs = consultationRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findByTitleContaining(String keyword, LocalDate startDate,
                                                               LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findByContentsContaining(String keyword, LocalDate startDate,
                                                                  LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findByTitleOrPlanOrClassContents(String keyword,
                                                                          LocalDate startDate,
                                                                          LocalDate endDate,
                                                                          Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Consultation> logs = consultationRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ConsultationResponseDto::of)
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
        log.info("url = {}", url);
        consultation.clearConsultationImages();

        return consultationImageRepository.save(ConsultationImage.builder()
                .consultationImageUrl(url)
                .consultation(consultation)
                .originalFileName(originalFileName)
                .build());
    }

    @Transactional(readOnly = true)
    public ConsultationSliceResponseDto readConsultationsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                                LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Consultation> allConsultations = consultationRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        int numberOfConsultation = consultations.size();
        List<ConsultationResponseDto> responseDtos = allConsultations.getContent().stream()
                .map(ConsultationResponseDto::of).toList();

        return ConsultationSliceResponseDto.builder()
                .consultations(responseDtos)
                .numberOfConsultation(numberOfConsultation)
                .page(allConsultations.getPageable().getPageNumber())
                .isLast(allConsultations.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> readDailyConsultations(Long userId, Long scheduleId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return consultations.stream()
                .map(ConsultationResponseDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> readMonthlyConsultations(Long userId, Long scheduleId, LocalDate date) {
        List<Consultation> consultations = consultationRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return consultations.stream()
                .map(ConsultationResponseDto::of).toList();
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    private Consultation findConsultationByIdAndUserId(Long consultationId, Long userId) {
        return consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(ConsultationErrorCode.CONSULTATION_NOT_FOUNT));
    }
}
