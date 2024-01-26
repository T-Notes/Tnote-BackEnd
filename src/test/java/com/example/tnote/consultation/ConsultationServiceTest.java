package com.example.tnote.consultation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.consultation.ConsultationErrorResult;
import com.example.tnote.base.exception.consultation.ConsultationException;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private ConsultationImageRepository consultationImageRepository;

    @InjectMocks
    private ConsultationService consultationService;

    @DisplayName("상담일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        LocalDateTime now = LocalDateTime.now();

        ConsultationRequestDto requestDto = ConsultationRequestDto.builder()
                .studentName("김태")
                .startDate(now)
                .endDate(now.plusHours(2))
                .counselingField(CounselingField.FRIENDSHIP)
                .counselingType(CounselingType.STUDENT)
                .consultationContents("상담내용")
                .consultationResult("상담결과")
                .isAllDay(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Consultation consultation = requestDto.toEntity(mockUser);
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        ConsultationResponseDto result = consultationService.save(userId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getStudentName()).isEqualTo(requestDto.getStudentName());
        verify(consultationRepository).save(any(Consultation.class));
    }


}
