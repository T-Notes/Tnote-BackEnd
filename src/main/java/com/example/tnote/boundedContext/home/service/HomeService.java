package com.example.tnote.boundedContext.home.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.repository.ConsultationQueryRepository;
import com.example.tnote.boundedContext.home.repository.ObservationQueryRepository;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final ConsultationQueryRepository consultationQueryRepository;
    private final ObservationQueryRepository observationQueryRepository;
    private final UserRepository userRepository;
    private final ClassLogService classLogService;
    private final ProceedingService proceedingService;
    private final ConsultationService consultationService;
    private final ObservationService observationService;

    public List<ConsultationResponseDto> findAllOfConsultation(String studentName, PrincipalDetails user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserException(UserErrorResult.USER_NOT_FOUND));

        List<Consultation> consultations = consultationQueryRepository.findAll(studentName);

        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    public List<ObservationResponseDto> findAllOfObservation(String studentName, PrincipalDetails user) {

        userRepository.findById(user.getId()).orElseThrow(
                () -> new UserException(UserErrorResult.USER_NOT_FOUND));

        List<Observation> observations = observationQueryRepository.findAll(studentName);

        return observations.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    public ArchiveResponseDto readDailyLogs(Long userId, LocalDate date) {
        List<ClassLogResponseDto> classLogs = classLogService.readDailyClassLogs(userId, date);
        List<ConsultationResponseDto> consultations = consultationService.readDailyConsultations(userId, date);
        List<ObservationResponseDto> observations = observationService.readDailyObservations(userId, date);
        List<ProceedingResponseDto> proceedings = proceedingService.readDailyProceedings(userId, date);

        return ArchiveResponseDto.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .build();
    }

}
