package com.example.tnote.classLog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSaveRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponses;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequest;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
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
import java.time.LocalDateTime;
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
        LocalDateTime startDate = LocalDate.of(2024, 1, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2024, 1, 3).atStartOfDay();

        ClassLogSaveRequest requestDto = new ClassLogSaveRequest(
                "color",
                false,
                "테스트 제출 과제",
                "테스트 학습 계획",
                "콘텐츠",
                "계획",
                startDate,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                "테스트 수업 로그"
        );


        when(userRepository.findUserById(userId)).thenReturn(mockUser);
        when(scheduleRepository.findScheduleById(1L)).thenReturn(mockSchedule);

        ClassLog classLog = requestDto.toEntity(mockUser, mockSchedule);
        when(classLogRepository.save(any(ClassLog.class))).thenReturn(classLog);

        ClassLogResponse result = classLogService.save(userId, 1L, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(classLogRepository).save(any(ClassLog.class));
    }

    @DisplayName("학급일지 조회: 작성자가 작성한 모든 학급일지 조회 확인")
    @Test
    void getClassLogs() {
        Pageable pageable = PageRequest.of(0, 10);

        ClassLog mockClassLog1 = mock(ClassLog.class);
        ClassLog mockClassLog2 = mock(ClassLog.class);
        List<ClassLog> mockClassLogsList = Arrays.asList(mockClassLog1, mockClassLog2);
        Slice<ClassLog> mockClassLogs = new PageImpl<>(mockClassLogsList, pageable, mockClassLogsList.size());

        when(classLogRepository.findAllByScheduleId(1L, pageable)).thenReturn(mockClassLogs);

        ClassLogResponses result = classLogService.findAll(userId, 1L, pageable);

        assertThat(result.getClassLogs())
                .isNotNull()
                .hasSize(2);

        verify(classLogRepository).findAllByScheduleId(1L, pageable);
    }

    @DisplayName("학급일지 상세 조회: 학급일지 상세 정보 조회 확인")
    @Test
    void getClassLogDetails_Success() {
        when(mockClassLog.getId()).thenReturn(classLogId);
        when(mockClassLog.getSchedule()).thenReturn(mockSchedule);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogResponse result = classLogService.find(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
    }

    @DisplayName("존재하지 않는 학급일지의 상세정보 조회 시 예외 발생")
    @Test
    void find_Fail() {
        Long NonClassLogId = 100L;

        when(classLogRepository.findByIdAndUserId(NonClassLogId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classLogService.find(userId, NonClassLogId))
                .isInstanceOf(ClassLogException.class);
    }

    @DisplayName("학급일지 삭제: 학급일지 삭제 작업 확인")
    @Test
    void delete() {
        when(mockClassLog.getId()).thenReturn(classLogId);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogDeleteResponse result = classLogService.delete(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
        verify(classLogRepository).delete(mockClassLog);
    }

    @DisplayName("학급일지 수정: 요청된 값에 따른 학급일지 수정 성공")
    @Test
    void update() {
        when(mockClassLog.getSchedule()).thenReturn(mockSchedule);
        ClassLogUpdateRequest classLogUpdateRequestDto = mock(ClassLogUpdateRequest.class);
        List<MultipartFile> classLogImages = Collections.emptyList();

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogResponse result = classLogService.update(userId, classLogId, classLogUpdateRequestDto,
                classLogImages);

        assertThat(result).isNotNull();
        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
    }

}
