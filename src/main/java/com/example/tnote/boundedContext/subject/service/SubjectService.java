package com.example.tnote.boundedContext.subject.service;


import static com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode.SCHEDULE_NOT_FOUND;
import static com.example.tnote.boundedContext.subject.exception.SubjectErrorCode.SUBJECT_NOT_FOUND;
import static com.example.tnote.boundedContext.subject.exception.SubjectErrorCode.WRONG_CLASS_TIME;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.dto.SubjectDetailResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsDeleteResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.repository.SubjectQueryRepository;
import com.example.tnote.boundedContext.subject.repository.SubjectRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public SubjectResponseDto addSubjects(SubjectRequestDto dto, Long scheduleId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        Schedule schedule = checkCurrentSchedule(scheduleId);

        compareLastClass(dto.getClassTime(), schedule.getLastClass());

        Subjects subjects = dto.toEntity(schedule);

        return SubjectResponseDto.of(subjectRepository.save(subjects));
    }

    @Transactional
    public SubjectResponseDto updateSubjects(SubjectsUpdateRequestDto dto, Long subjectsId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Subjects subjects = authorization(subjectsId, currentUser.getId());

        compareLastClass(dto.getClassTime(), subjects.getSchedule().getLastClass());

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
    public SubjectsDeleteResponseDto deleteSubjects(Long scheduleId, Long subjectsId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Subjects subject = authorization(subjectsId, currentUser.getId());
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomException(SCHEDULE_NOT_FOUND));

        if (!subject.getSchedule().equals(schedule)) {
            log.warn("해당하는 학기가 존재하지 않습니다");
            throw new CustomException(SCHEDULE_NOT_FOUND);
        }

        subjectRepository.deleteById(subject.getId());

        return SubjectsDeleteResponseDto.of(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponseDto> getMyClass(Long scheduleId, ClassDay day, Long userId) {
        matchUserWithSchedule(scheduleId, userId);

        return SubjectResponseDto.of(
                subjectQueryRepository.findAllByScheduleIdAndUserIdAndClassDay(scheduleId, userId, day));
    }


    @Transactional(readOnly = true)
    public SubjectDetailResponseDto getSubject(Long scheduleId, Long subjectId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);
        Subjects subject = authorization(subjectId, userId);

        return SubjectDetailResponseDto.of(subject);
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND));
    }

    private Schedule checkCurrentSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomException(SCHEDULE_NOT_FOUND));
    }

    private void matchUserWithSchedule(Long scheduleId, Long userId) {
        User user = checkCurrentUser(userId);
        Schedule schedule = checkCurrentSchedule(scheduleId);

        if (!schedule.getUser().equals(user)) {
            log.warn("스케쥴 user와 현 user가 다릅니다");
            throw new CustomException(SCHEDULE_NOT_FOUND);
        }
    }

    private Subjects authorization(Long id, Long userId) {

        Subjects subjects = subjectRepository.findById(id).orElseThrow(
                () -> new CustomException(SUBJECT_NOT_FOUND));

        if (!subjects.getSchedule().getUser().getId().equals(userId)) {
            log.warn("member doesn't have authentication , user {}", subjects.getSchedule().getUser());
            throw new CustomException(USER_NOT_FOUND);
        }
        return subjects;

    }

    private int extractNumber(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new CustomException(WRONG_CLASS_TIME);
    }

    private void compareLastClass(String subjectLastClass, String scheduleLastClass) {
        if (extractNumber(subjectLastClass) > extractNumber(scheduleLastClass)) {
            throw new CustomException(WRONG_CLASS_TIME);
        }
    }
}
