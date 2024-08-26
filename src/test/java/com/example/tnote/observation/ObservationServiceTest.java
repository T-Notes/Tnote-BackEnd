package com.example.tnote.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tnote.boundedContext.observation.dto.ObservationDeleteResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationSaveRequest;
import com.example.tnote.boundedContext.observation.dto.ObservationResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationResponses;
import com.example.tnote.boundedContext.observation.dto.ObservationUpdateRequest;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import com.example.tnote.boundedContext.observation.exception.ObservationException;
import com.example.tnote.boundedContext.observation.repository.ObservationImageRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
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
public class ObservationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private ObservationImageRepository observationImageRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private RecentLogService recentLogService;

    @InjectMocks
    private ObservationService observationService;

    private User mockUser;
    private Schedule mockSchedule;
    private Observation mockObservation;
    private Long userId = 1L;
    private Long scheduleId = 2L;
    private Long observationId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = mock(User.class);
        mockSchedule = new Schedule(1L, "1학기", null,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 3, 28),
                mockUser);

        mockObservation = mock(Observation.class);
    }

    @DisplayName("관찰일지 저장: 정상적인 경우 성공적으로 저장 확인")
    @Test
    void save() {
        ObservationSaveRequest requestDto = new ObservationSaveRequest("김태환",
                mockSchedule.getStartDate().atStartOfDay(), mockSchedule.getStartDate().atStartOfDay().plusHours(2),
                "컨텐츠", "지도", true, "red");


        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        Observation observation = requestDto.toEntity(mockUser, mockSchedule);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(mockSchedule));
        when(observationRepository.save(any(Observation.class))).thenReturn(observation);

        ObservationResponse result = observationService.save(userId, scheduleId, requestDto,
                Collections.emptyList());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        verify(observationRepository).save(any(Observation.class));
    }

    @DisplayName("관찰일지 조회: 작성자가 작성한 모든 관찰일지 확인")
    @Test
    void getLogs() {
        Pageable pageable = PageRequest.of(0, 10);

        Observation mockObservation1 = mock(Observation.class);
        Observation mockObservation2 = mock(Observation.class);

        Observation otherObservation = mock(Observation.class);

        List<Observation> mockObservationList = Arrays.asList(mockObservation1, mockObservation2);
        Slice<Observation> mockObservations = new PageImpl<>(mockObservationList, pageable, mockObservationList.size());

        when(observationRepository.findAllByScheduleId(scheduleId, pageable)).thenReturn(mockObservations);
        ObservationResponses result = observationService.findAll(userId, scheduleId, pageable);

        assertThat(result.getObservations())
                .isNotNull()
                .hasSize(2);

        verify(observationRepository).findAllByScheduleId(scheduleId, pageable);
    }

    @DisplayName("관찰일지 상세 조회: 관찰일지 상세 정보 조회 확인")
    @Test
    void getDetails() {
        when(mockUser.getId()).thenReturn(userId);
        when(mockObservation.getId()).thenReturn(observationId);
        when(mockObservation.getUser()).thenReturn(mockUser);
        when(mockObservation.getSchedule()).thenReturn(mockSchedule);

        ObservationImage mockObservationImage = mock(ObservationImage.class);

        List<ObservationImage> mockObservationImages = List.of(mockObservationImage);

        when(observationRepository.findByIdAndUserId(userId, observationId)).thenReturn(Optional.of(mockObservation));
        when(observationImageRepository.findObservationImageByObservationId(observationId)).thenReturn(
                mockObservationImages);

        ObservationResponse result = observationService.find(userId, observationId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(observationId);

        verify(observationRepository).findByIdAndUserId(userId, observationId);
        verify(observationImageRepository).findObservationImageByObservationId(observationId);
    }

    @DisplayName("존재하지 않는 관찰일지의 상세정보 조회 시 예외 발생")
    @Test
    void getDetailException() {
        Long userId = 1L;
        Long observationId = 100L;

        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> observationService.find(userId, observationId))
                .isInstanceOf(ObservationException.class);
    }

    @DisplayName("관찰일지 삭제: 관찰일지 삭제 작업 확인")
    @Test
    void delete() {
        when(mockObservation.getId()).thenReturn(observationId);
        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.of(mockObservation));

        ObservationDeleteResponse result = observationService.delete(userId, observationId);

        verify(observationRepository).findByIdAndUserId(observationId, userId);
        verify(observationRepository).delete(mockObservation);
    }

    @DisplayName("관찰일지 수정: 요청된 값에 따른 관찰일지 수정 확인")
    @Test
    void update() {
        ObservationUpdateRequest observationUpdateRequestDto = mock(ObservationUpdateRequest.class);
        List<MultipartFile> observationImages = Collections.emptyList();

        when(observationRepository.findByIdAndUserId(observationId, userId)).thenReturn(Optional.of(mockObservation));
        when(mockObservation.getSchedule()).thenReturn(mockSchedule);

        ObservationResponse result = observationService.update(userId, observationId,
                observationUpdateRequestDto,
                observationImages);

        assertThat(result).isNotNull();
        verify(observationRepository).findByIdAndUserId(observationId, userId);
    }
}
