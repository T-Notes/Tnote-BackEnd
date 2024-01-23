package com.example.tnote.classLog;

import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ClassLogServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassLogRepository classLogRepository;

    @InjectMocks
    private ClassLogService classLogService;

    @Test
    void saveClassLogTest() {
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

        // 검증
        assertNotNull(result);
        verify(classLogRepository).save(any(ClassLog.class));
    }


}
