package com.example.tnote.proceeding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSaveRequest;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.exception.ProceedingException;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private User mockUser;
    private Schedule mockSchedule;
    private Proceeding mockProceeding;
    private Long userId = 1L;
    private Long scheduleId = 2L;
    private Long proceedingId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockSchedule = new Schedule(1L, "1학기", null,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 28),
                mockUser,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());

        mockProceeding = mock(Proceeding.class);
    }

    @DisplayName("업무일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {

        ProceedingSaveRequest requestDto = new ProceedingSaveRequest("테스트 수업 로그",
                mockSchedule.getStartDate().atStartOfDay(), mockSchedule.getStartDate().atStartOfDay().plusHours(2),
                "부산",
                "사진찍기", false, "red");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Proceeding proceeding = requestDto.toEntity(mockUser, mockSchedule);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        when(proceedingRepository.save(any(Proceeding.class))).thenReturn(proceeding);

        ProceedingResponse result = proceedingService.save(userId, scheduleId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(proceedingRepository).save(any(Proceeding.class));
    }

    @DisplayName("업무일지 조회: 작성자가 작성한 모든 업무일지 조회 확인")
    @Test
    void getProceedings() {
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
        when(mockUser.getId()).thenReturn(userId);
        when(mockProceeding.getId()).thenReturn(proceedingId);
        when(mockProceeding.getSchedule()).thenReturn(mockSchedule);
        when(mockProceeding.getUser()).thenReturn(mockUser);

        ProceedingImage mockProceedingImage = mock(ProceedingImage.class);

        List<ProceedingImage> mockProceedingImages = List.of(mockProceedingImage);

        when(proceedingRepository.findByIdAndUserId(proceedingId, userId)).thenReturn(Optional.of(mockProceeding));
        when(proceedingImageRepository.findProceedingImageByProceedingId(proceedingId)).thenReturn(
                mockProceedingImages);

        ProceedingDetailResponseDto result = proceedingService.getProceedingDetail(userId, proceedingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proceedingId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProceedingImageUrls()).hasSize(mockProceedingImages.size());

        verify(proceedingRepository).findByIdAndUserId(proceedingId, userId);
        verify(proceedingImageRepository).findProceedingImageByProceedingId(proceedingId);
    }

    @DisplayName("존재하지 않는 업무일지의 상세정보 조회 시 예외 발생")
    @Test
    void getClassLogDetailException() {
        Long proceedingId = 100L;

        when(proceedingRepository.findByIdAndUserId(proceedingId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proceedingService.getProceedingDetail(userId, proceedingId))
                .isInstanceOf(ProceedingException.class);
    }

    @DisplayName("업무일지 삭제: 업무일지 삭제 작업 확인")
    @Test
    void deleteProceeding() {
        when(mockProceeding.getId()).thenReturn(proceedingId);
        when(proceedingRepository.findByIdAndUserId(userId, proceedingId)).thenReturn(Optional.of(mockProceeding));

        ProceedingDeleteResponseDto result = proceedingService.deleteProceeding(userId, proceedingId);

        verify(proceedingRepository).findByIdAndUserId(userId, proceedingId);
        verify(proceedingRepository).delete(mockProceeding);
    }

    @DisplayName("업무일지 수정: 요청된 값에 따른 업무일지 수정 확인")
    @Test
    void updateClassLog() {
        ProceedingUpdateRequestDto proceedingUpdateRequestDto = mock(ProceedingUpdateRequestDto.class);
        List<MultipartFile> proceedingImages = Collections.emptyList();

        when(proceedingRepository.findByIdAndUserId(userId, proceedingId)).thenReturn(Optional.of(mockProceeding));
        when(mockProceeding.getSchedule()).thenReturn(mockSchedule);

        ProceedingResponse result = proceedingService.updateProceeding(userId, proceedingId,
                proceedingUpdateRequestDto,
                proceedingImages);

        assertThat(result).isNotNull();
        verify(proceedingRepository).findByIdAndUserId(userId, proceedingId);
    }
}
