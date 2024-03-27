package com.example.tnote.proceeding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
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
public class ProceedingServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProceedingRepository proceedingRepository;
    @Mock
    private ProceedingImageRepository proceedingImageRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private RecentLogService recentLogService;
    @InjectMocks
    private ProceedingService proceedingService;


    @DisplayName("업무일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        Long scheduleId = 2L;
        User mockUser = mock(User.class);
        Schedule mockSchedule = mock(Schedule.class);

        LocalDateTime now = LocalDateTime.now();

        ProceedingRequestDto requestDto = ProceedingRequestDto.builder()
                .title("테스트 수업 로그")
                .startDate(now)
                .endDate(now.plusHours(2))
                .location("부산")
                .workContents("사진찍기")
                .isAllDay(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Proceeding proceeding = requestDto.toEntity(mockUser, mockSchedule);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        when(proceedingRepository.save(any(Proceeding.class))).thenReturn(proceeding);

        ProceedingResponseDto result = proceedingService.save(userId, scheduleId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(proceedingRepository).save(any(Proceeding.class));
    }

    @DisplayName("업무일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        Long scheduleId = 2L;
        ProceedingRequestDto requestDto = mock(ProceedingRequestDto.class);
        List<MultipartFile> proceedingImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> proceedingService.save(userId, scheduleId, requestDto, proceedingImages));

        verify(userRepository).findById(userId);
        verify(proceedingRepository, never()).save(any(Proceeding.class));
    }

    @DisplayName("업무일지 조회: 작성자가 작성한 모든 업무일지 조회 확인")
    @Test
    void getProceedings() {
        Long userId = 1L;
        Long scheduleId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        Proceeding mockProceeding1 = mock(Proceeding.class);
        Proceeding mockProceeding2 = mock(Proceeding.class);
        List<Proceeding> mockProceedingList = Arrays.asList(mockProceeding1, mockProceeding2);
        Slice<Proceeding> mockProceedings = new PageImpl<>(mockProceedingList, pageable, mockProceedingList.size());

        when(proceedingRepository.findAllByScheduleId(scheduleId, pageable)).thenReturn(mockProceedings);
        ProceedingSliceResponseDto result = proceedingService.readAllProceeding(userId, scheduleId, pageable);

        assertThat(result.getProceedings())
                .isNotNull()
                .hasSize(2);

        verify(proceedingRepository).findAllByScheduleId(scheduleId, pageable);
    }

    @DisplayName("업무일지 상세 조회: 업무일지 상세 정보 조회 확인")
    @Test
    void getProceedingDetails() {
        Long userId = 1L;
        Long proceedingId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Proceeding mockProceeding = mock(Proceeding.class);
        when(mockProceeding.getId()).thenReturn(proceedingId);
        when(mockProceeding.getUser()).thenReturn(mockUser);

        ProceedingImage mockProceedingImage = mock(ProceedingImage.class);

        List<ProceedingImage> mockProceedingImages = List.of(mockProceedingImage);

        when(proceedingRepository.findByIdAndUserId(proceedingId, userId)).thenReturn(Optional.of(mockProceeding));
        when(proceedingImageRepository.findProceedingImageById(proceedingId)).thenReturn(mockProceedingImages);

        ProceedingDetailResponseDto result = proceedingService.getProceedingDetails(userId, proceedingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proceedingId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProceedingImageUrls()).hasSize(mockProceedingImages.size());

        verify(proceedingRepository).findByIdAndUserId(proceedingId, userId);
        verify(proceedingImageRepository).findProceedingImageById(proceedingId);
    }

    @DisplayName("존재하지 않는 업무일지의 상세정보 조회 시 예외 발생")
    @Test
    void getClassLogDetailException() {
        Long userId = 1L;
        Long proceedingId = 100L;

        when(proceedingRepository.findByIdAndUserId(proceedingId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proceedingService.getProceedingDetails(userId, proceedingId))
                .isInstanceOf(CustomException.class);
    }

    @DisplayName("업무일지 삭제: 업무일지 삭제 작업 확인")
    @Test
    void deleteProceeding() {
        Long userId = 1L;
        Long proceedingId = 1L;

        Proceeding mockProceeding = mock(Proceeding.class);
        when(mockProceeding.getId()).thenReturn(proceedingId);

        when(proceedingRepository.findByIdAndUserId(userId, proceedingId)).thenReturn(Optional.of(mockProceeding));

        ProceedingDeleteResponseDto result = proceedingService.deleteProceeding(userId, proceedingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proceedingId);

        verify(proceedingRepository).findByIdAndUserId(userId, proceedingId);
        verify(proceedingRepository).delete(mockProceeding);
    }

    @DisplayName("업무일지 수정: 요청된 값에 따른 업무일지 수정 확인")
    @Test
    void updateClassLog() {
        Long userId = 1L;
        Long proceedingId = 1L;
        Proceeding mockProceeding = mock(Proceeding.class);
        ProceedingUpdateRequestDto proceedingUpdateRequestDto = mock(ProceedingUpdateRequestDto.class);
        List<MultipartFile> proceedingImages = Collections.emptyList();

        when(proceedingRepository.findByIdAndUserId(userId, proceedingId)).thenReturn(Optional.of(mockProceeding));

        ProceedingResponseDto result = proceedingService.updateProceeding(userId, proceedingId,
                proceedingUpdateRequestDto,
                proceedingImages);

        assertThat(result).isNotNull();
        verify(proceedingRepository).findByIdAndUserId(userId, proceedingId);
    }
}
