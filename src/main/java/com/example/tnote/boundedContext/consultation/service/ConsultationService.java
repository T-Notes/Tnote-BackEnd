package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.exception.classLog.ClassLogErrorResult;
import com.example.tnote.base.exception.classLog.ClassLogException;
import com.example.tnote.base.exception.consultation.ConsultationErrorResult;
import com.example.tnote.base.exception.consultation.ConsultationException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
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
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ConsultationImageRepository consultationImageRepository;
    private final UserRepository userRepository;

    public ConsultationResponseDto save(Long userId, ConsultationRequestDto requestDto,
                                        List<MultipartFile> consultationImages) {
        requestDto.validateEnums();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Consultation consultation = requestDto.toEntity(user);
        if (consultationImages != null && !consultationImages.isEmpty()) {
            List<ConsultationImage> uploadedImages = uploadConsultationImages(consultation, consultationImages);
            consultation.getConsultationImage().addAll(uploadedImages); // 이미지 리스트에 추가
        }
        return ConsultationResponseDto.of(consultationRepository.save(consultation));
    }

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> readAllConsultation(Long userId) {
        //todo slice 형태로 바꿔야합니다
        List<Consultation> consultations = consultationRepository.findAllByUserId(userId);

        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
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
        ;
        updateEachItems(consultation, requestDto, consultationImages);
        return ConsultationResponseDto.of(consultation);
    }

    private void updateEachItems(Consultation consultation, ConsultationUpdateRequestDto requestDto,
                                 List<MultipartFile> consultationImages) {
        if (requestDto.hasConsultationContents()) {
            consultation.updateConsultationContents(requestDto.getConsultationContents());
        }
        if (requestDto.hasConsultationResult()) {
            consultation.updateConsultationResult(requestDto.getConsultationResult());
        }
        if (consultationImages != null && !consultationImages.isEmpty()) {
            consultation.updateConsultationImages(
                    deleteExistedImagesAndUploadNewImages(consultation, consultationImages));
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

    public List<ConsultationResponseDto> readDailyConsultations(Long userId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Consultation> consultations = consultationRepository.findByUserIdAndStartDateBetween(userId, startOfDay,
                endOfDay);
        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
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
