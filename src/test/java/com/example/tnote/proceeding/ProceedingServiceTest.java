package com.example.tnote.proceeding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.proceeding.ProceedingException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
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

@ExtendWith(MockitoExtension.class)
public class ProceedingServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ProceedingRepository proceedingRepository;
    @Mock
    private ProceedingImageRepository proceedingImageRepository;
    @InjectMocks
    private ProceedingService proceedingService;


    @DisplayName("업무일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        User mockUser = mock(User.class);

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
        Proceeding proceeding = requestDto.toEntity(mockUser);
        when(proceedingRepository.save(any(Proceeding.class))).thenReturn(proceeding);

        // 테스트 실행
        ProceedingResponseDto result = proceedingService.save(userId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(proceedingRepository).save(any(Proceeding.class));
    }

    @DisplayName("업무일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        ProceedingRequestDto requestDto = mock(ProceedingRequestDto.class);
        List<MultipartFile> proceedingImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserException.class)
                .isThrownBy(() -> proceedingService.save(userId, requestDto, proceedingImages));

        verify(userRepository).findById(userId);
        verify(proceedingRepository, never()).save(any(Proceeding.class));
    }

    @DisplayName("업무일지 조회: 작성자가 작성한 모든 업ㅔ일지 조회 확인")
    @Test
    void getProceedings() {
        Long userId = 1L;

        Proceeding mockProceeding1 = mock(Proceeding.class);
        Proceeding mockProceeding2 = mock(Proceeding.class);
        List<Proceeding> mockProceedings = Arrays.asList(mockProceeding1, mockProceeding2);

        when(proceedingRepository.findAllByUserId(userId)).thenReturn(mockProceedings);
        List<ProceedingResponseDto> result = proceedingService.readAllProceeding(userId);

        assertThat(result)
                .isNotNull()
                .hasSize(2);

        verify(proceedingRepository).findAllByUserId(userId);
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

        when(proceedingRepository.findByIdAndUserId(userId, proceedingId)).thenReturn(Optional.of(mockProceeding));
        when(proceedingImageRepository.findProceedingImageById(proceedingId)).thenReturn(mockProceedingImages);

        ProceedingDetailResponseDto result = proceedingService.getProceedingDetails(userId, proceedingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(proceedingId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getProceedingImageUrls()).hasSize(mockProceedingImages.size());

        verify(proceedingRepository).findByIdAndUserId(userId, proceedingId);
        verify(proceedingImageRepository).findProceedingImageById(proceedingId);
    }

    @DisplayName("존재하지 않는 업무일지의 상세정보 조회 시 예외 발생")
    @Test
    void getClassLogDetailException() {
        Long userId = 1L;
        Long proceedingId = 100L;

        when(proceedingRepository.findByIdAndUserId(proceedingId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> proceedingService.getProceedingDetails(userId, proceedingId))
                .isInstanceOf(ProceedingException.class);
    }
    @DisplayName("업무일지 삭제: 업무일지 삭제 작업 확인")
    @Test
    void deleteClassLog() {
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
}
