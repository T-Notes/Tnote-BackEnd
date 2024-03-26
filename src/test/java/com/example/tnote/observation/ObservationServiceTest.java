package com.example.tnote.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.observation.ObservationException;
import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationDetailResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationRequestDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequestDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.observation.service.ObservationService;
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
public class ObservationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private ObservationImageRepository observationImageRepository;
    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ObservationService observationService;

    @DisplayName("관찰일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        Long userId = 1L;
        Long scheduleId = 2L;
        User mockUser = mock(User.class);
        Schedule mockSchedule = mock(Schedule.class);

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
        Observation observation = requestDto.toEntity(mockUser, mockSchedule);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        when(observationRepository.save(any(Observation.class))).thenReturn(observation);

        ObservationResponseDto result = observationService.save(userId, scheduleId, requestDto,
                Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getStudentName()).isEqualTo(requestDto.getStudentName());
        verify(observationRepository).save(any(Observation.class));
    }

    @DisplayName("관찰일지 저장: 존재하지 않는 사용자로 인한 예외 발생 확인")
    @Test
    void noUserSave() {
        Long userId = 1L;
        Long scheduleId = 2L;
        ObservationRequestDto requestDto = mock(ObservationRequestDto.class);
        List<MultipartFile> observationImages = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(CustomException.class)
                .isThrownBy(() -> observationService.save(userId, scheduleId, requestDto, observationImages));

        verify(userRepository).findById(userId);
        verify(observationRepository, never()).save(any(Observation.class));
    }

    @DisplayName("관찰일지 조회: 작성자가 작성한 모든 관찰일지 확인")
    @Test
    void getLogs() {
        Long userId = 1L;
        Long scheduleId = 2L;
        Long otherUserId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        Observation mockObservation1 = mock(Observation.class);
        Observation mockObservation2 = mock(Observation.class);

        Observation otherObservation = mock(Observation.class);

        List<Observation> mockObservationList = Arrays.asList(mockObservation1, mockObservation2);
        Slice<Observation> mockObservations = new PageImpl<>(mockObservationList, pageable, mockObservationList.size());

        when(observationRepository.findAllByScheduleId(scheduleId, pageable)).thenReturn(mockObservations);
        ObservationSliceResponseDto result = observationService.readAllObservation(userId, scheduleId, pageable);

        assertThat(result.getObservations())
                .isNotNull()
                .hasSize(2);

        verify(observationRepository).findAllByScheduleId(scheduleId, pageable);
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

    @DisplayName("존재하지 않는 관찰일지의 상세정보 조회 시 예외 발생")
    @Test
    void getDetailException() {
        Long userId = 1L;
        Long observationId = 100L;

        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> observationService.readObservationDetail(userId, observationId))
                .isInstanceOf(ObservationException.class);
    }

    @DisplayName("관찰일지 삭제: 관찰일지 삭제 작업 확인")
    @Test
    void delete() {
        Long userId = 1L;
        Long observationId = 1L;

        Observation mockObservation = mock(Observation.class);
        when(mockObservation.getId()).thenReturn(observationId);
        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.of(mockObservation));

        ObservationDeleteResponseDto result = observationService.deleteObservation(userId, observationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(observationId);

        verify(observationRepository).findByIdAndUserId(observationId, userId);
        verify(observationRepository).delete(mockObservation);
    }

    @DisplayName("관찰일지 수정: 요청된 값에 따른 관찰일지 수정 확인")
    @Test
    void update() {
        Long userId = 1L;
        Long observationId = 1L;
        Observation mockObservation = mock(Observation.class);
        ObservationUpdateRequestDto observationUpdateRequestDto = mock(ObservationUpdateRequestDto.class);
        List<MultipartFile> observationImages = Collections.emptyList();

        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.of(mockObservation));

        ObservationResponseDto result = observationService.updateObservation(userId, observationId,
                observationUpdateRequestDto,
                observationImages);

        assertThat(result).isNotNull();
        verify(observationRepository).findByIdAndUserId(observationId, userId);
    }
}
