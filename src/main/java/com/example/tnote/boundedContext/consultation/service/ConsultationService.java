package com.example.tnote.boundedContext.consultation.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonErrorResult.USER_NOT_FOUND));
        Consultation consultation = Consultation.builder()
                .user(user)
                .studentName(requestDto.getStudentName())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .counselingField(requestDto.getCounselingField())
                .consultationResult(requestDto.getConsultationResult())
                .consultationContents(requestDto.getConsultationContents())
                .consultationResult(requestDto.getConsultationResult())
                .build();

        return ConsultationResponseDto.of(consultationRepository.save(consultation));
    }
}
