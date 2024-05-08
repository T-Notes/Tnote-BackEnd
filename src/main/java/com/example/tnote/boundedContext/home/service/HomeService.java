package com.example.tnote.boundedContext.home.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.home.constant.LogType;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.home.repository.ClassLogQueryRepository;
import com.example.tnote.boundedContext.home.repository.ConsultationQueryRepository;
import com.example.tnote.boundedContext.home.repository.LastScheduleRepository;
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
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoSliceResponseDto;
import com.example.tnote.boundedContext.todo.service.TodoService;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
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
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ClassLogService classLogService;
    private final ProceedingService proceedingService;
    private final ConsultationService consultationService;
    private final ObservationService observationService;
    private final TodoService todoService;
    private final LastScheduleRepository lastScheduleRepository;

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
                () -> CustomException.USER_NOT_FOUND);
    }

    public ArchiveSliceResponseDto readLogsByDate(Long userId, Long scheduleId, LocalDate startDate, LocalDate endDate,
                                                  LogType logType, Pageable pageable) {
        if (logType == LogType.CLASS_LOG) {
            ClassLogSliceResponseDto classLogs = classLogService.readClassLogsByDate(userId, scheduleId, startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().classLogs(classLogs).build();
        }
        if (logType == LogType.CONSULTATION) {
            ConsultationSliceResponseDto consultations = consultationService.readConsultationsByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().consultations(consultations).build();
        }
        if (logType == LogType.OBSERVATION) {
            ObservationSliceResponseDto observations = observationService.readObservationsByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().observations(observations).build();
        }
        if (logType == LogType.PROCEEDING) {
            ProceedingSliceResponseDto proceedings = proceedingService.readProceedingsByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().proceedings(proceedings).build();
        }
        if (logType == LogType.TODO) {
            TodoSliceResponseDto todos = todoService.readTodosByDate(userId, scheduleId, startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().todos(todos).build();
        }
        return null;
    }

    public ArchiveResponseDto readDailyLogs(Long userId, Long scheduleId, LocalDate date) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> CustomException.SCHEDULE_NOT_FOUND);
        LocalDate startDate = schedule.getStartDate();
        LocalDate endDate = schedule.getEndDate();

        if (date.isBefore(startDate) || (endDate != null && date.isAfter(endDate))) {
            throw new CustomException(ErrorCode.DATES_NOT_INCLUDED_IN_SEMESTER);
        }
        List<ClassLogResponseDto> classLogs = classLogService.readDailyClassLog(userId, scheduleId, date);
        List<ConsultationResponseDto> consultations = consultationService.readDailyConsultations(userId, scheduleId,
                date);
        List<ObservationResponseDto> observations = observationService.readDailyObservations(userId, scheduleId, date);
        List<ProceedingResponseDto> proceedings = proceedingService.readDailyProceedings(userId, scheduleId, date);
        List<TodoResponseDto> todos = todoService.readDailyTodos(userId, scheduleId, date);

        return ArchiveResponseDto.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .todos(todos)
                .build();
    }

    public ArchiveResponseDto readMonthlyLogs(Long userId, Long scheduleId, LocalDate date) {
        List<ClassLogResponseDto> classLogs = classLogService.readMonthlyClassLog(userId, scheduleId, date);
        List<ConsultationResponseDto> consultations = consultationService.readMonthlyConsultations(userId, scheduleId,
                date);
        List<ObservationResponseDto> observations = observationService.readMonthlyObservations(userId, scheduleId,
                date);
        List<ProceedingResponseDto> proceedings = proceedingService.readMonthlyProceedings(userId, scheduleId, date);
        List<TodoResponseDto> todos = todoService.readMonthlyTodos(userId, scheduleId, date);

        return ArchiveResponseDto.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .todos(todos)
                .build();
    }

//    @Transactional
//    public LastScheduleResponseDto saveLastSchedule(Long userId, Long scheduleId) {
//
//        Schedule schedule = scheduleRepository.findById(scheduleId)
//                .orElseThrow(() -> CustomException.SCHEDULE_NOT_FOUND);
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> CustomException.USER_NOT_FOUND);
//
//        LastSchedule lastInfo = LastSchedule.builder()
//                .schedule(schedule)
//                .user(user)
//                .build();
//
//        return LastScheduleResponseDto.of(lastScheduleRepository.save(lastInfo));
//    }
//
//    @Transactional(readOnly = true)
//    public LastScheduleResponseDto getLastSchedule(Long userId, Long scheduleId) {
//        LastSchedule lastSchedule = lastScheduleRepository.findByUserIdAndScheduleId(userId, scheduleId)
//                .orElseThrow(() -> CustomException.SCHEDULE_NOT_FOUND);
//
//        return LastScheduleResponseDto.of(lastSchedule);
//    }
}
