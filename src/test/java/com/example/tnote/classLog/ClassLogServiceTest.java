package com.example.tnote.classLog;

import com.example.tnote.base.exception.classLog.ClassLogException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
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
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassLogServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassLogRepository classLogRepository;
    @Mock
    private ClassLogImageRepository classLogImageRepository;

    @InjectMocks
    private ClassLogService classLogService;

    @DisplayName("학급일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        LocalDateTime now = LocalDateTime.now();

        ClassLogRequestDto requestDto = ClassLogRequestDto.builder()
                .title("테스트 수업 로그")
                .startDate(now)
                .endDate(now.plusHours(2))
                .plan("테스트 학습 계획")
                .classContents("테스트 수업 내용")
                .submission("테스트 제출 과제")
                .magnitude("테스트 진도")
                .isAllDay(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        ClassLog classLog = requestDto.toEntity(mockUser);
        when(classLogRepository.save(any(ClassLog.class))).thenReturn(classLog);

        // 테스트 실행
        ClassLogResponseDto result = classLogService.save(userId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(classLogRepository).save(any(ClassLog.class));
    }

    @DisplayName("학급일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        ClassLogRequestDto requestDto = mock(ClassLogRequestDto.class);
        List<MultipartFile> classLogImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserException.class)
                .isThrownBy(() -> classLogService.save(userId, requestDto, classLogImages));

        verify(userRepository).findById(userId);
        verify(classLogRepository, never()).save(any(ClassLog.class));
    }

    @DisplayName("학급일지 조회: 작성자가 작성한 모든 학급일지 조회 확인")
    @Test
    void getClassLogs() {
        Long userId = 1L;

        ClassLog mockClassLog1 = mock(ClassLog.class);
        ClassLog mockClassLog2 = mock(ClassLog.class);
        List<ClassLog> mockClassLogs = Arrays.asList(mockClassLog1, mockClassLog2);

        when(classLogRepository.findAllByUserId(userId)).thenReturn(mockClassLogs);
        List<ClassLogResponseDto> result = classLogService.readAllClassLog(userId);

        assertThat(result)
                .isNotNull()
                .hasSize(2);

        verify(classLogRepository).findAllByUserId(userId);
    }

    @DisplayName("학급일지 상세 조회: 학급일지 상세 정보 조회 확인")
    @Test
    void getClassLogDetails() {
        Long userId = 1L;
        Long classLogId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        ClassLog mockClassLog = mock(ClassLog.class);
        when(mockClassLog.getId()).thenReturn(classLogId);
        when(mockClassLog.getUser()).thenReturn(mockUser);

        ClassLogImage mockClassLogImage1 = mock(ClassLogImage.class);
        ClassLogImage mockClassLogImage2 = mock(ClassLogImage.class);

        List<ClassLogImage> mockClassLogImages = List.of(mockClassLogImage1, mockClassLogImage2);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));
        when(classLogImageRepository.findClassLogImagesByClassLogId(classLogId)).thenReturn(mockClassLogImages);

        ClassLogDetailResponseDto result = classLogService.getClassLogDetail(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getClassLogImageUrls()).hasSize(mockClassLogImages.size());

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
        verify(classLogImageRepository).findClassLogImagesByClassLogId(classLogId);
    }

    @DisplayName("존재하지 않는 학급일지의 상세정보 조회 시 예외 발생")
    @Test
    void getClassLogDetailException() {
        Long userId = 1L;
        Long classLogId = 100L;

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> classLogService.getClassLogDetail(userId, classLogId))
                .isInstanceOf(ClassLogException.class);
    }

    @DisplayName("학급일지 삭제: 학급일지 삭제 작업 확인")
    @Test
    void deleteClassLog() {
        Long userId = 1L;
        Long classLogId = 1L;

        ClassLog mockClassLog = mock(ClassLog.class);
        when(mockClassLog.getId()).thenReturn(classLogId);

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogDeleteResponseDto result = classLogService.deleteClassLog(userId, classLogId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(classLogId);

        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
        verify(classLogRepository).delete(mockClassLog);
    }

    @DisplayName("학급일지 수정: 요청된 값에 따른 학급일지 수정 확인")
    @Test
    void updateClassLog() {
        Long userId = 1L;
        Long classLogId = 1L;
        ClassLog mockClassLog = mock(ClassLog.class);
        ClassLogUpdateRequestDto classLogUpdateRequestDto = mock(ClassLogUpdateRequestDto.class);
        List<MultipartFile> classLogImages = Collections.emptyList();

        when(classLogRepository.findByIdAndUserId(classLogId, userId)).thenReturn(Optional.of(mockClassLog));

        ClassLogResponseDto result = classLogService.updateClassLog(userId, classLogId, classLogUpdateRequestDto,
                classLogImages);

        assertThat(result).isNotNull();
        verify(classLogRepository).findByIdAndUserId(userId, classLogId);
    }

}
