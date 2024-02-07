package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.exception.classLog.ClassLogErrorResult;
import com.example.tnote.base.exception.classLog.ClassLogException;
import com.example.tnote.base.exception.consultation.ConsultationErrorResult;
import com.example.tnote.base.exception.consultation.ConsultationException;
import com.example.tnote.base.exception.schedule.ScheduleErrorResult;
import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.io.IOException;
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

    public ConsultationResponseDto save(Long userId, Long scheduleId, ConsultationRequestDto requestDto,
                                        List<MultipartFile> consultationImages) {
        requestDto.validateEnums();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(
                ScheduleErrorResult.SCHEDULE_NOT_FOUND));

        Consultation consultation = requestDto.toEntity(user, schedule);
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> uploadedImages = uploadConsultationImages(consultation, consultationImages);
            consultation.getConsultationImage().addAll(uploadedImages);
        }
        return ConsultationResponseDto.of(consultationRepository.save(consultation));
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

    public ConsultationDeleteResponseDto deleteClassLog(Long userId, Long consultationId) {
        Consultation consultation = consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(
                        ConsultationErrorResult.CONSULTATION_NOT_FOUNT));
        ;
        consultationRepository.delete(consultation);

        return ConsultationDeleteResponseDto.builder()
                .id(consultation.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public ConsultationDetailResponseDto getConsultationDetail(Long userId, Long consultationId) {
        Consultation consultation = consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(
                        ConsultationErrorResult.CONSULTATION_NOT_FOUNT));
        List<ConsultationImage> consultationImages = consultationImageRepository.findConsultationImageByConsultationId(
                consultationId);
        return new ConsultationDetailResponseDto(consultation, consultationImages);
    }

    public ConsultationResponseDto updateConsultation(Long userId, Long consultationId,
                                                      ConsultationUpdateRequestDto requestDto,
                                                      List<MultipartFile> consultationImages) {
        Consultation consultation = consultationRepository.findByIdAndUserId(consultationId, userId)
                .orElseThrow(() -> new ConsultationException(
                        ConsultationErrorResult.CONSULTATION_NOT_FOUNT));

        updateConsultationItem(requestDto, consultation, consultationImages);
        return ConsultationResponseDto.of(consultation);
    }

    private void updateConsultationItem(ConsultationUpdateRequestDto requestDto, Consultation consultation,
                                        List<MultipartFile> consultationImages) {
        updateConsultationFields(requestDto, consultation);
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> updatedImages = deleteExistedImagesAndUploadNewImages(consultation,
                    consultationImages);
            consultation.updateConsultationImages(updatedImages);
        }
    }

    private void updateConsultationFields(ConsultationUpdateRequestDto requestDto, Consultation consultation) {
        if (requestDto.hasStudentName()) {
            consultation.updateStudentName(requestDto.getStudentName());
        }
        if (requestDto.hasStartDate()) {
            consultation.updateStartDate(requestDto.getStartDate());
        }
        if (requestDto.hasEndDate()) {
            consultation.updateEndDate(requestDto.getEndDate());
        }
        if (requestDto.hasConsultationContents()) {
            consultation.updateConsultationContents(requestDto.getConsultationContents());
        }
        if (requestDto.hasConsultationResult()) {
            consultation.updateConsultationResult(requestDto.getConsultationResult());
        }
        if (requestDto.hasCounselingField()) {
            requestDto.validateEnums();
            consultation.updateCounselingField(requestDto.getCounselingField());
        }
        if (requestDto.hasCounselingType()) {
            requestDto.validateEnums();
            consultation.updateCounselingType(requestDto.getCounselingType());
        }
    }

    private List<ConsultationImage> uploadConsultationImages(Consultation consultation,
                                                             List<MultipartFile> consultationImages) {
        return consultationImages.stream()
                .map(file -> createConsultationImage(consultation, file))
                .toList();
    }

    private ConsultationImage createConsultationImage(Consultation consultation, MultipartFile file) {
        String url;
        try {
            url = FileUploadUtils.saveFileAndGetUrl(file);
        } catch (IOException e) {
            log.error("File upload fail", e);
            throw new IllegalArgumentException();
        }

        log.info("url = {}", url);
        consultation.clearConsultationImages();

        return consultationImageRepository.save(ConsultationImage.builder()
                .consultationImageUrl(url)
                .consultation(consultation)
                .build());
    }

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

    private List<ConsultationImage> deleteExistedImagesAndUploadNewImages(Consultation consultation,
                                                                          List<MultipartFile> consultationImages) {
        deleteExistedImages(consultation);
        return uploadConsultationImages(consultation, consultationImages);
    }

    private void deleteExistedImages(Consultation consultation) {
        consultationImageRepository.deleteByConsultationId(consultation.getId());
    }
}
