package com.example.tnote.consultation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.repository.ConsultationImageRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @DisplayName("상담일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        Long scheduleId = 2L;
        User mockUser = mock(User.class);
        Schedule mockSchedule = mock(Schedule.class);

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
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        Consultation consultation = requestDto.toEntity(mockUser, mockSchedule);
        when(consultationRepository.save(any(Consultation.class))).thenReturn(consultation);

        ConsultationResponseDto result = consultationService.save(userId, scheduleId, requestDto,
                Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getStudentName()).isEqualTo(requestDto.getStudentName());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @DisplayName("상담일지 저장: 올바르지 않은 상수가 입력되었을 때 예외 발생 확인")
    @Test
    void saveWithInvalidEnums() {
        Long userId = 1L;
        Long scheduleId = 2L;
        LocalDateTime now = LocalDateTime.now();

        ConsultationRequestDto requestDto = ConsultationRequestDto.builder()
                .studentName("김태")
                .startDate(now)
                .endDate(now.plusHours(2))
                .counselingField(null)
                .counselingType(null)
                .consultationContents("상담내용")
                .consultationResult("상담결과")
                .isAllDay(false)
                .build();

        assertThatThrownBy(() -> consultationService.save(userId, scheduleId, requestDto, Collections.emptyList()))
                .isInstanceOf(CustomException.class);
    }

    @DisplayName("상담일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        Long scheduleId = 2L;
        ConsultationRequestDto requestDto = mock(ConsultationRequestDto.class);
        List<MultipartFile> consultationImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> consultationService.save(userId, scheduleId, requestDto, consultationImages));

        verify(userRepository).findById(userId);
        verify(consultationRepository, never()).save(any(Consultation.class));
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
        ConsultationSliceResponseDto result = consultationService.readAllConsultation(userId, scheduleId, pageable);

        assertThat(result.getConsultations())
                .isNotNull()
                .hasSize(2);

        verify(consultationRepository).findAllByScheduleId(scheduleId, pageable);
    }

    @DisplayName("상담일지 상세 조회: 상담일지 상세 정보 조회 확인")
    @Test
    void getConsultationDetails() {
        Long userId = 1L;
        Long consultationId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Consultation mockConsultation = mock(Consultation.class);
        when(mockConsultation.getId()).thenReturn(consultationId);
        when(mockConsultation.getUser()).thenReturn(mockUser);

        List<ConsultationImage> mockClassLogImages = new ArrayList<>();

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));
        when(consultationImageRepository.findConsultationImageByConsultationId(consultationId)).thenReturn(
                mockClassLogImages);

        ConsultationDetailResponseDto result = consultationService.getConsultationDetail(userId, consultationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(consultationId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getConsultationImageUrls()).hasSize(mockClassLogImages.size());

        verify(consultationRepository).findByIdAndUserId(consultationId, userId);
        verify(consultationImageRepository).findConsultationImageByConsultationId(consultationId);
    }

    @DisplayName("존재하지 않는 상담일지의 상세정보 조회 시 예외 발생")
    @Test
    void getConsultationDetailException() {
        Long userId = 1L;
        Long consultationId = 100L;

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultationService.getConsultationDetail(userId, consultationId))
                .isInstanceOf(CustomException.class);
    }

    @DisplayName("상담일지 삭제: 상담일지 삭제 작업 확인")
    @Test
    void deleteConsultation() {
        Long userId = 1L;
        Long consultationId = 1L;

        Consultation mockConsultation = mock(Consultation.class);
        when(mockConsultation.getId()).thenReturn(consultationId);

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));

        ConsultationDeleteResponseDto result = consultationService.deleteConsultation(userId, consultationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(consultationId);

        verify(consultationRepository).findByIdAndUserId(userId, consultationId);
        verify(consultationRepository).delete(mockConsultation);
    }

    @DisplayName("상담일지 수정: 요청된 값에 따른 상담일지 수정 확인")
    @Test
    void updateConsultation() {
        Long userId = 1L;
        Long consultationId = 1L;
        Consultation mockConsultation = mock(Consultation.class);
        ConsultationUpdateRequestDto classLogUpdateRequestDto = mock(ConsultationUpdateRequestDto.class);
        List<MultipartFile> consultationImages = Collections.emptyList();

        when(consultationRepository.findByIdAndUserId(consultationId, userId)).thenReturn(
                Optional.of(mockConsultation));

        ConsultationResponseDto result = consultationService.updateConsultation(userId, consultationId,
                classLogUpdateRequestDto,
                consultationImages);

        assertThat(result).isNotNull();
        verify(consultationRepository).findByIdAndUserId(userId, consultationId);
    }
}
