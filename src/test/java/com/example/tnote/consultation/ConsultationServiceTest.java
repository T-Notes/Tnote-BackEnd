package com.example.tnote.consultation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponses;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSaveRequest;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequest;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.exception.ConsultationException;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class ConsultationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private ConsultationImageRepository consultationImageRepository;
    @Mock
    private RecentLogService recentLogService;

    @InjectMocks
    private ConsultationService consultationService;

    private User mockUser;
    private Schedule mockSchedule;
    private Consultation mockConsultation;
    private Long userId = 1L;
    private Long consultationId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockSchedule = new Schedule(1L, "1학기", null,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 28),
                mockUser);

        mockConsultation = mock(Consultation.class);
    }

    @DisplayName("상담일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        LocalDateTime startDate = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2024, 1, 3).atStartOfDay();
        ConsultationSaveRequest requestDto = new ConsultationSaveRequest("김", startDate, endDate,
                CounselingField.FRIENDSHIP, CounselingType.STUDENT, "상담", "결과", false, "red");

        when(userRepository.findUserById(userId)).thenReturn(mockUser);
        when(scheduleRepository.findScheduleById(1L)).thenReturn(mockSchedule);

        Consultation consultation = requestDto.toEntity(mockUser, mockSchedule);
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        ConsultationResponse result = consultationService.save(userId, 1L, requestDto,
                Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @DisplayName("상담일지 조회: 작성자가 작성한 모든 상담일지 조회 확인")
    @Test
    void getConsultations() {
        Long userId = 1L;
        Long scheduleId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        Consultation mockConsultation1 = mock(Consultation.class);
        Consultation mockConsultation2 = mock(Consultation.class);
        List<Consultation> mockConsultationList = Arrays.asList(mockConsultation1, mockConsultation2);
        Slice<Consultation> mockConsultations = new PageImpl<>(mockConsultationList, pageable,
                mockConsultationList.size());

        when(consultationRepository.findAllByScheduleId(scheduleId, pageable)).thenReturn(mockConsultations);
        ConsultationResponses result = consultationService.findAll(userId, scheduleId, pageable);

        assertThat(result.getConsultations())
                .isNotNull()
                .hasSize(2);

        verify(consultationRepository).findAllByScheduleId(scheduleId, pageable);
    }

    @DisplayName("상담일지 상세 조회: 상담일지 상세 정보 조회 확인")
    @Test
    void getConsultationDetails() {
        when(mockConsultation.getId()).thenReturn(consultationId);
        when(mockConsultation.getSchedule()).thenReturn(mockSchedule);

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));

        ConsultationResponse result = consultationService.find(userId, consultationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(consultationId);

        verify(consultationRepository).findByIdAndUserId(consultationId, userId);
    }

    @DisplayName("존재하지 않는 상담일지의 상세정보 조회 시 예외 발생")
    @Test
    void getConsultationDetailException() {
        Long consultationId = 100L;

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.find(userId, consultationId))
                .isInstanceOf(ConsultationException.class);
    }

    @DisplayName("상담일지 삭제: 상담일지 삭제 작업 확인")
    @Test
    void deleteConsultation() {
        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));

        ConsultationDeleteResponse result = consultationService.delete(userId, consultationId);

        verify(consultationRepository).findByIdAndUserId(userId, consultationId);
        verify(consultationRepository).delete(mockConsultation);
    }

    @DisplayName("상담일지 수정: 요청된 값에 따른 상담일지 수정 확인")
    @Test
    void updateConsultation() {
        when(mockConsultation.getId()).thenReturn(consultationId);
        when(mockConsultation.getSchedule()).thenReturn(mockSchedule);
        ConsultationUpdateRequest classLogUpdateRequestDto = mock(ConsultationUpdateRequest.class);
        List<MultipartFile> consultationImages = Collections.emptyList();

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));

        ConsultationResponse result = consultationService.update(userId, consultationId,
                classLogUpdateRequestDto,
                consultationImages);

        assertThat(result).isNotNull();
        verify(consultationRepository).findByIdAndUserId(userId, consultationId);
    }
}
