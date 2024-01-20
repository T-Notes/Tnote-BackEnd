package com.example.tnote.boundedContext.subject.service;

import com.example.tnote.base.exception.ScheduleErrorResult;
import com.example.tnote.base.exception.ScheduleException;
import com.example.tnote.base.exception.SubjectsErrorResult;
import com.example.tnote.base.exception.SubjectsException;
import com.example.tnote.base.exception.TodoErrorResult;
import com.example.tnote.base.exception.TodoException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsDeleteResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.repository.SubjectQueryRepository;
import com.example.tnote.boundedContext.subject.repository.SubjectRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectQueryRepository subjectQueryRepository;

    @Transactional
    public SubjectResponseDto addSubjects(SubjectRequestDto dto) {

        Schedule currentSchedule = checkCurrentSchedule(dto.getScheduleId());

        Subjects subjects = Subjects.builder()
                .memo(dto.getMemo())
                .classLocation(dto.getClassLocation())
                .classTime(dto.getClassTime())
                .classDay(dto.getClassDay())
                .subjectName(dto.getSubjectName())
                .color(dto.getColor())
                .date(dto.getDate())
                .schedule(currentSchedule)
                .build();

        return SubjectResponseDto.of(subjectRepository.save(subjects));
    }

    @Transactional
    public SubjectResponseDto updateSubjects(SubjectsUpdateRequestDto dto, Long subjectsId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Subjects subjects = authorization(subjectsId, currentUser);

        updateEachSubjectsItem(dto, subjects);

        return SubjectResponseDto.of(subjects);
    }

    private void updateEachSubjectsItem(SubjectsUpdateRequestDto dto, Subjects subjects) {
        if (dto.hasMemo()) {
            subjects.updateMemo(dto.getMemo());
        }
        if (dto.hasSubjectName()) {
            subjects.updateSubjectName(dto.getSubjectName());
        }
        if (dto.hasDate()) {
            subjects.updateDate(dto.getDate());
        }
        if (dto.hasClassDay()) {
            subjects.updateClassDay(dto.getClassDay());
        }
        if (dto.hasClassLocation()) {
            subjects.updateClassLocation(dto.getClassLocation());
        }
        if (dto.hasClassTime()) {
            subjects.updateClassTime(dto.getClassTime());
        }
        if (dto.hasColor()) {
            subjects.updateColor(dto.getColor());
        }
    }

    @Transactional
    public SubjectsDeleteResponseDto deleteSubjects(Long scheduleId, Long subjectsId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Subjects subject = authorization(subjectsId, currentUser);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();

        if (!subject.getSchedule().equals(schedule)) {
            log.warn("해당하는 학기가 존재하지 않습니다");
            throw new TodoException(TodoErrorResult.SCHEDULE_NOT_FOUND);
        }

        subjectRepository.deleteById(subject.getId());

        return SubjectsDeleteResponseDto.builder()
                .id(subject.getId())
                .build();
    }

    private Subjects authorization(Long id, User member) {

        Subjects subjects = subjectRepository.findById(id).orElseThrow(
                () -> new SubjectsException(SubjectsErrorResult.SUBJECT_NOT_FOUND));

        if (!subjects.getSchedule().getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", subjects.getSchedule().getUser());
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
        return subjects;

    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    private Schedule checkCurrentSchedule(Long scheduleId) {
        Optional<Schedule> currentSchedule = scheduleRepository.findById(scheduleId);

        if (currentSchedule.isEmpty()) {
            log.warn("해당하는 학기가 없습니다. currentSchedule : {}", currentSchedule);
            throw new ScheduleException(ScheduleErrorResult.SCHEDULE_NOT_FOUND);
        }

        return currentSchedule.get();
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getMyClass(Long scheduleId, ClassDay day, PrincipalDetails user) {
        return SubjectResponseDto.of(
                subjectQueryRepository.findAllByScheduleIdAndUserIdAndClassDay(scheduleId, user.getId(), day));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getTodayClass(Long scheduleId, PrincipalDetails user, LocalDate date) {

        if (date.equals(LocalDate.now())) {
            return SubjectResponseDto.of(
                    subjectQueryRepository.findAllByScheduleIdAndUserIdAndDate(scheduleId, user.getId(), date));
        }
        throw new SubjectsException(SubjectsErrorResult.TODAY_IS_WRONG_WITH_DATE);

    }
}
