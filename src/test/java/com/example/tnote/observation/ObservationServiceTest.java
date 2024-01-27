package com.example.tnote.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.observation.service.ObservationService;
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
public class ObservationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private ObservationImageRepository observationImageRepository;

    @InjectMocks
    private ObservationService observationService;
    @DisplayName("관찰일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        User mockUser = mock(User.class);

        LocalDateTime now = LocalDateTime.now();

        ObservationRequestDto requestDto = ObservationRequestDto.builder()
                .studentName("김태환")
                .startDate(now)
                .endDate(now.plusHours(2))
                .guidance("지도")
                .observationContents("관찰했음")
                .isAllDay(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Observation observation = requestDto.toEntity(mockUser);
        when(observationRepository.save(any(Observation.class))).thenReturn(observation);

        ObservationResponseDto result = observationService.save(userId, requestDto, Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getStudentName()).isEqualTo(requestDto.getStudentName());
        verify(observationRepository).save(any(Observation.class));
    }
    @DisplayName("관찰일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        ObservationRequestDto requestDto = mock(ObservationRequestDto.class);
        List<MultipartFile> observationImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserException.class)
                .isThrownBy(() -> observationService.save(userId, requestDto, observationImages));

        verify(userRepository).findById(userId);
        verify(observationRepository, never()).save(any(Observation.class));
    }
    @DisplayName("관찰일지 조회: 작성자가 작성한 모든 관찰일지 확인")
    @Test
    void getLogs() {
        Long userId = 1L;
        Long otherUserId = 2L;

        Observation mockObservation1 = mock(Observation.class);
        Observation mockObservation2 = mock(Observation.class);

        Observation otherObservation = mock(Observation.class);

        List<Observation> mockObservations = Arrays.asList(mockObservation1, mockObservation2);

        when(observationRepository.findAllByUserId(userId)).thenReturn(mockObservations);
        List<ObservationResponseDto> result = observationService.readAllObservation(userId);

        assertThat(result)
                .isNotNull()
                .hasSize(2);

        verify(observationRepository).findAllByUserId(userId);
    }
    @DisplayName("관찰일지 상세 조회: 관찰일지 상세 정보 조회 확인")
    @Test
    void getDetails() {
        Long userId = 1L;
        Long observationId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Observation mockObservation = mock(Observation.class);
        when(mockObservation.getId()).thenReturn(observationId);
        when(mockObservation.getUser()).thenReturn(mockUser);

        ObservationImage mockObservationImage = mock(ObservationImage.class);

        List<ObservationImage> mockObservationImages = List.of(mockObservationImage);

        when(observationRepository.findByIdAndUserId(userId, observationId)).thenReturn(Optional.of(mockObservation));
        when(observationImageRepository.findObservationImageById(observationId)).thenReturn(mockObservationImages);

        ObservationDetailResponseDto result = observationService.readObservationDetail(userId, observationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(observationId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getObservationImageUrls()).hasSize(mockObservationImages.size());

        verify(observationRepository).findByIdAndUserId(userId, observationId);
        verify(observationImageRepository).findObservationImageById(observationId);
    }

}
