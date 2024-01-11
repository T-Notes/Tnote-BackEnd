package com.example.tnote.boundedContext.schedule.service;

import com.example.tnote.base.exception.*;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.schedule.dto.SubjectResponseDto;
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

        Subjects saved = subjectRepository.save(subjects);

        return SubjectResponseDto.of(saved);
    }

    @Transactional
    public SubjectResponseDto updateSubjects(SubjectRequestDto dto, Long subjectsId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Subjects subjects = authorization(subjectsId, currentUser);
        //이 부분의 메소드들 checkCurrentUser,authorization 이 두 메소드는 이 클래스 내에서만 사용되면 접근제한자를 private로 바꾸시는건 어떨까요?? public으로 두신 이유가 궁금합니다!

        // 이부분도 똑같아욥!

        if(dto.getSubjectName() != null) {
            subjects.updateSubjectName(dto.getSubjectName());
        }
        if(dto.getClassDay() != null) {
            subjects.updateClassDay(dto.getClassDay());
        }
        if(dto.getClassTime() != null) {
            subjects.updateClassTime(dto.getClassTime());
        }
        if(dto.getClassLocation() != null) {
            subjects.updateClassLocation(dto.getClassLocation());
        }
        if(dto.getMemo() != null) {
            subjects.updateMemo(dto.getMemo());
        }

        return SubjectResponseDto.of(subjects);
    }

    @Transactional
    public String deleteSubjects(Long subjectsId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Subjects own = authorization(subjectsId, currentUser);

        subjectRepository.deleteById(own.getId());

        return "과목이 삭제되었습니다.";
    }

    @Transactional
    public Subjects authorization(Long id, User member) {

        Subjects subjects = subjectRepository.findById(id).orElseThrow(
                () -> new SubjectsException(SubjectsErrorResult.SUBJECT_NOT_FOUND));

        if (!subjects.getSchedule().getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", subjects.getSchedule().getUser());
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
        return subjects;

    }

    @Transactional
    public User checkCurrentUser(Long id) {
        Optional<User> currentUser = userRepository.findById(id);

        if (currentUser.isEmpty()) {
            log.warn("해당하는 유저가 없습니다. currentUser : {}", currentUser);
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        return currentUser.get();
    }

    @Transactional
    public Schedule checkCurrentSchedule(Long scheduleId) {
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
