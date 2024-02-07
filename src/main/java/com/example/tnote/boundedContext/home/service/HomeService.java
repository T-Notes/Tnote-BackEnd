package com.example.tnote.boundedContext.home.service;

import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.repository.ClassLogQueryRepository;
import com.example.tnote.boundedContext.home.repository.ConsultationQueryRepository;
import com.example.tnote.boundedContext.home.repository.ObservationQueryRepository;
import com.example.tnote.boundedContext.home.repository.ProceedingQueryRepository;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final ConsultationQueryRepository consultationQueryRepository;
    private final ObservationQueryRepository observationQueryRepository;
    private final ClassLogQueryRepository classLogQueryRepository;
    private final ProceedingQueryRepository proceedingQueryRepository;
    private final UserRepository userRepository;
    private final ClassLogService classLogService;
    private final ProceedingService proceedingService;
    private final ConsultationService consultationService;
    private final ObservationService observationService;

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findAllOfConsultation(String studentName, Long userId) {

        findUser(userId);

        List<Consultation> consultations = consultationQueryRepository.findAll(studentName);

        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findAllOfObservation(String studentName, Long userId) {

        findUser(userId);

        List<Observation> observations = observationQueryRepository.findAll(studentName);

        return observations.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponseDto> findAllOfClassLog(String title, Long userId) {

        findUser(userId);

        List<ClassLog> classLogs = classLogQueryRepository.findAll(title);

        return classLogs.stream()
                .map(ClassLogResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> findAllOfProceeding(String title, Long userId) {

        findUser(userId);

        List<Proceeding> proceedings = proceedingQueryRepository.findAll(title);

        return proceedings.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

    private void findUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    public ArchiveResponseDto readDailyLogs(Long userId, Long scheduleId, LocalDate startDate, LocalDate endDate,
                                            Pageable pageable) {
        ClassLogSliceResponseDto classLogs = classLogService.readDailyClassLogs(userId, scheduleId, startDate,
                endDate, pageable);
        ConsultationSliceResponseDto consultations = consultationService.readDailyConsultations(userId, scheduleId, startDate,
                endDate, pageable);
        ObservationSliceResponseDto observations = observationService.readDailyObservations(userId, scheduleId, startDate,
                endDate, pageable);
        ProceedingSliceResponseDto proceedings = proceedingService.readDailyProceedings(userId, scheduleId, startDate,
                endDate, pageable);

        return ArchiveResponseDto.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .build();
    }


}
