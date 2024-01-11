package com.example.tnote.boundedContext.schedule.service;

import com.example.tnote.base.exception.*;
import com.example.tnote.boundedContext.schedule.dto.*;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.entity.Subjects;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.schedule.repository.SubjectQueryRepository;
import com.example.tnote.boundedContext.schedule.repository.SubjectRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        if (dto.hasMemo()){
            subjects.updateMemo(dto.getMemo());
        }
        if (dto.hasSubjectName()){
            subjects.updateSubjectName(dto.getSubjectName());
        }
        if (dto.hasClassDay()){
            subjects.updateClassDay(dto.getClassDay());
        }
        if (dto.hasClassLocation()){
            subjects.updateClassLocation(dto.getClassLocation());
        }
        if (dto.hasClassTime()){
            subjects.updateClassTime(dto.getClassTime());
        }
    }

    @Transactional
    public SubjectsDeleteResponseDto deleteSubjects(Long subjectsId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Subjects own = authorization(subjectsId, currentUser);

        subjectRepository.deleteById(own.getId());

        return SubjectsDeleteResponseDto.builder()
                .id(own.getId())
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
        Optional<User> currentUser = userRepository.findById(id);

        if (currentUser.isEmpty()) {
            log.warn("해당하는 유저가 없습니다. currentUser : {}", currentUser);
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        return currentUser.get();
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
        return SubjectResponseDto.of(subjectQueryRepository.findAllByScheduleIdAndUserIdAndClassDay(scheduleId, user.getId(), day));
    }
}
