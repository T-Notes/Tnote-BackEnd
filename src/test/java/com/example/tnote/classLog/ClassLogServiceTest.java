package com.example.tnote.classLog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSaveRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
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
public class ClassLogServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassLogRepository classLogRepository;
    @Mock
    private ClassLogImageRepository classLogImageRepository;
    @Mock
    ScheduleRepository scheduleRepository;

    @InjectMocks
    private ClassLogService classLogService;

    @Mock
    private RecentLogService recentLogService;

    private User mockUser;
    private Schedule mockSchedule;
    private ClassLog mockClassLog;
    private Long userId = 1L;
    private Long scheduleId = 2L;
    private Long classLogId = 1L;

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

        mockClassLog = mock(ClassLog.class);
    }

    @DisplayName("학급일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save_Success() {
        ClassLogSaveRequest requestDto = ClassLogSaveRequest.builder()
                .title("테스트 수업 로그")
                .startDate(mockSchedule.getStartDate().atStartOfDay())
                .endDate(mockSchedule.getStartDate().atStartOfDay().plusHours(2))
                .plan("테스트 학습 계획")
                .classContents("테스트 수업 내용")
                .submission("테스트 제출 과제")
                .magnitude("테스트 진도")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        ClassLog classLog = requestDto.toEntity(mockUser, mockSchedule);
        when(classLogRepository.save(any(ClassLog.class))).thenReturn(classLog);

        ClassLogResponseDto result = classLogService.save(userId, scheduleId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(classLogRepository).save(any(ClassLog.class));
    }


    @DisplayName("학급일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void save_Fail() {
        ClassLogSaveRequest requestDto = mock(ClassLogSaveRequest.class);
        List<MultipartFile> classLogImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomExceptions.class)
                .isThrownBy(() -> classLogService.save(userId, scheduleId, requestDto, classLogImages));

        verify(userRepository).findById(userId);
        verify(classLogRepository, never()).save(any(ClassLog.class));
    }

    @DisplayName("학급일지 조회: 작성자가 작성한 모든 학급일지 조회 확인")
    @Test
    void getClassLogs() {
        Pageable pageable = PageRequest.of(0, 10);

        ClassLog mockClassLog1 = mock(ClassLog.class);
        ClassLog mockClassLog2 = mock(ClassLog.class);
        List<ClassLog> mockClassLogsList = Arrays.asList(mockClassLog1, mockClassLog2);
        Slice<ClassLog> mockClassLogs = new PageImpl<>(mockClassLogsList, pageable, mockClassLogsList.size());

        when(classLogRepository.findAllByScheduleId(scheduleId, pageable)).thenReturn(mockClassLogs);

        ClassLogSliceResponseDto result = classLogService.readAllClassLog(userId, scheduleId, pageable);

        assertThat(result.getClassLogs())
                .isNotNull()
                .hasSize(2);

        verify(classLogRepository).findAllByScheduleId(scheduleId, pageable);
    }

    @DisplayName("학급일지 상세 조회: 학급일지 상세 정보 조회 확인")
    @Test
    void getClassLogDetails_Success() {
        when(mockUser.getId()).thenReturn(userId);
        when(mockClassLog.getId()).thenReturn(classLogId);
        when(mockClassLog.getUser()).thenReturn(mockUser);
        when(mockClassLog.getSchedule()).thenReturn(mockSchedule);

        ClassLogImage mockClassLogImage1 = mock(ClassLogImage.class);
        ClassLogImage mockClassLogImage2 = mock(ClassLogImage.class);

        List<ClassLogImage> mockClassLogImages = List.of(mockClassLogImage1, mockClassLogImage2);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));
        when(classLogImageRepository.findClassLogImagesByClassLogId(classLogId)).thenReturn(mockClassLogImages);

        ClassLogDetailResponseDto result = classLogService.getClassLogDetail(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);
        assertThat(result.getUserId()).isEqualTo(userId);

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
        verify(classLogImageRepository).findClassLogImagesByClassLogId(classLogId);
    }

    @DisplayName("존재하지 않는 학급일지의 상세정보 조회 시 예외 발생")
    @Test
    void getClassLogDetail_Fail() {
        Long NonClassLogId = 100L;

        when(classLogRepository.findByIdAndUserId(NonClassLogId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classLogService.getClassLogDetail(userId, NonClassLogId))
                .isInstanceOf(ClassLogException.class);
    }

    @DisplayName("학급일지 삭제: 학급일지 삭제 작업 확인")
    @Test
    void deleteClassLog() {
        when(mockClassLog.getId()).thenReturn(classLogId);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogDeleteResponseDto result = classLogService.deleteClassLog(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
        verify(classLogRepository).delete(mockClassLog);
    }

    @DisplayName("학급일지 수정: 요청된 값에 따른 학급일지 수정 성공")
    @Test
    void updateClassLog() {
        when(mockClassLog.getSchedule()).thenReturn(mockSchedule);
        ClassLogUpdateRequestDto classLogUpdateRequestDto = mock(ClassLogUpdateRequestDto.class);
        List<MultipartFile> classLogImages = Collections.emptyList();

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogResponseDto result = classLogService.updateClassLog(userId, classLogId, classLogUpdateRequestDto,
                classLogImages);

        assertThat(result).isNotNull();
        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
    }

}
