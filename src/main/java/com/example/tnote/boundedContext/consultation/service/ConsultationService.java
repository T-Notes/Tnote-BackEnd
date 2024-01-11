package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
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
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ConsultationResponseDto save(Long userId, ConsultationRequestDto requestDto) {
        requestDto.validateEnums();
        // 아래 Exception도 classLog에 작성한 바와 같이 생각합니다!
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonErrorResult.USER_NOT_FOUND));

        LocalDateTime startDate = requestDto.getStartDate();
        LocalDateTime endDate = requestDto.getEndDate();
        if(requestDto.isAllDay()){
            startDate = startDate.withHour(12).withMinute(0);
            endDate = endDate.withHour(23).withMinute(59);
        }
        Consultation consultation = Consultation.builder()
                .user(user)
                .studentName(requestDto.getStudentName())
                .startDate(startDate)
                .endDate(endDate)
                .counselingField(requestDto.getCounselingField())
                .consultationResult(requestDto.getConsultationResult())
                .consultationContents(requestDto.getConsultationContents())
                .consultationResult(requestDto.getConsultationResult())
                .build();

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
        Consultation consultation = consultationRepository.findByIdAndUserId(userId, consultationId).orElseThrow();
        consultationRepository.delete(consultation);

        return ConsultationDeleteResponseDto.builder()
                .id(consultation.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public ConsultationDetailResponseDto getConsultationDetail(Long userId, Long consultationId) {
        Consultation consultation = consultationRepository.findByIdAndUserId(userId, consultationId).orElseThrow();
        return new ConsultationDetailResponseDto(consultation);
    }

    public ConsultationResponseDto updateConsultation(Long userId, Long consultationId,
                                                      ConsultationUpdateRequestDto requestDto) {
        Consultation consultation = consultationRepository.findByIdAndUserId(userId, consultationId).orElseThrow();
        updateEachItems(consultation, requestDto);
        return ConsultationResponseDto.of(consultation);
    }

    private void updateEachItems(Consultation consultation, ConsultationUpdateRequestDto requestDto) {
        if (requestDto.hasConsultationContents()) {
            consultation.updateConsultationContents(requestDto.getConsultationContents());
        }
        if (requestDto.hasConsultationResult()) {
            consultation.updateConsultationResult(requestDto.getConsultationResult());
        }
    }
}
