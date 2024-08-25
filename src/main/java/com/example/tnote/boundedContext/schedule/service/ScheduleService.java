package com.example.tnote.boundedContext.schedule.service;


import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.boundedContext.classLog.repository.query.ClassLogQueryRepository;
import com.example.tnote.boundedContext.consultation.repository.query.ConsultationQueryRepository;
import com.example.tnote.boundedContext.observation.repository.query.ObservationQueryRepository;
import com.example.tnote.boundedContext.proceeding.repository.query.ProceedingQueryRepository;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import com.example.tnote.boundedContext.schedule.dto.ScheduleDeleteResponse;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequest;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponse;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequest;
import com.example.tnote.boundedContext.schedule.dto.SemesterResponse;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleQueryRepository;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ClassLogQueryRepository classLogQueryRepository;
    private final ProceedingQueryRepository proceedingQueryRepository;
    private final ConsultationQueryRepository consultationQueryRepository;
    private final ObservationQueryRepository observationQueryRepository;
    private final RecentLogRepository recentLogRepository;

    @Transactional
    public ScheduleResponse saveSchedule(final ScheduleRequest dto, final Long userId) {

        Schedule schedule = dto.toEntity(userRepository.findUserById(userId));

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponse updateSchedule(final ScheduleUpdateRequest dto, final Long scheduleId,
                                           final Long userId) {

        Schedule schedule = authorizationWriter(scheduleId, userRepository.findUserById(userId));

        updateEachScheduleItem(dto, schedule);

        return ScheduleResponse.from(schedule);
    }

    private void updateEachScheduleItem(final ScheduleUpdateRequest dto, final Schedule schedule) {
        if (dto.hasSemesterName()) {
            schedule.updateSemesterName(dto.getSemesterName());
        }
        if (dto.hasLastClass()) {
            schedule.updateLastClass(dto.getLastClass());
        }
        if (dto.hasStartDate()) {
            schedule.updateStartDate(dto.getStartDate());
        }
        if (dto.hasEndDate()) {
            schedule.updateEndDate(dto.getEndDate());
        }
    }

    @Transactional
    public ScheduleDeleteResponse deleteSchedule(final Long scheduleId, final Long userId) {

        User user = userRepository.findUserById(userId);
        Schedule own = authorizationWriter(scheduleId, user);

        scheduleRepository.deleteById(own.getId());

        classLogQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        proceedingQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        consultationQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        observationQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        recentLogRepository.deleteAllByUserIdAndScheduleId(userId, scheduleId);

        if (user.getLastScheduleId() == scheduleId) {
            user.updateLastScheduleName(null);
            user.updateLastScheduleId(0);
        }

        return ScheduleDeleteResponse.from(own);
    }

    private Schedule authorizationWriter(final Long scheduleId, final User member) {

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        matchUserWithSchedule(schedule.getId(), member.getId());

        return schedule;

    }


    // 학기당 남은 일수
    @Transactional(readOnly = true)
    public long countLeftDays(final LocalDate date, final Long scheduleId, final Long userId) {

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        LocalDate CurrentDate = getLocalDate(date);

        matchUserWithSchedule(schedule.getId(), userId);

        return ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate()) <= 0 ? 0
                : ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate());

    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> getAllSubjectsBySchedule(final Long scheduleId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponse.from(scheduleQueryRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> searchSemester(final String semesterName, final Long userId) {

        List<Schedule> schedules = scheduleQueryRepository.findAllBySemesterNameAndUserId(semesterName, userId);

        return schedules.stream()
                .map(SemesterResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> findSchedule(final Long scheduleId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponse.excludeLastDayFrom(scheduleRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterResponse> findScheduleList(final Long userId) {
        return SemesterResponse.from(scheduleQueryRepository.findAllByUserId(userId));
    }

    @Transactional
    public long countLeftClasses(final LocalDate date, final Long userId, final Long scheduleId) {

        int totalCnt = 0;
        HashMap<String, Integer> map = new HashMap<>();
        map.put("MONDAY", 0);
        map.put("TUESDAY", 0);
        map.put("WEDNESDAY", 0);
        map.put("THURSDAY", 0);
        map.put("FRIDAY", 0);
        map.put("SATURDAY", 0);
        map.put("SUNDAY", 0);

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        for (LocalDate currentDate = date; !currentDate.isAfter(schedule.getEndDate());
             currentDate = currentDate.plusDays(1)) {
            String dayOfWeek = String.valueOf(currentDate.getDayOfWeek());

            map.put(dayOfWeek, map.get(dayOfWeek) + 1);
        }

        matchUserWithSchedule(schedule.getId(), userId);

        for (Subjects s : schedule.getSubjectsList()) {

            if (map.containsKey(String.valueOf(s.getClassDay()))) {
                totalCnt += map.get(String.valueOf(s.getClassDay()));
            }
        }

        return totalCnt;
    }

    private void matchUserWithSchedule(final Long scheduleId, final Long userId) {
        User currentUser = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if (!schedule.getUser().equals(currentUser)) {
            throw new ScheduleException(USER_NOT_FOUND);
        }
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }

}
