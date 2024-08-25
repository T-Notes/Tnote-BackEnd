package com.example.tnote.boundedContext.subject.service;


import static com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode.SCHEDULE_NOT_FOUND;
import static com.example.tnote.boundedContext.subject.exception.SubjectErrorCode.WRONG_CLASS_TIME;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.dto.SubjectDetailResponse;
import com.example.tnote.boundedContext.subject.dto.SubjectRequest;
import com.example.tnote.boundedContext.subject.dto.SubjectResponse;
import com.example.tnote.boundedContext.subject.dto.SubjectsDeleteResponse;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequest;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.exception.SubjectException;
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
    public SubjectResponse saveSubjects(final SubjectRequest dto, final Long scheduleId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        compareLastClass(dto.getClassTime(), schedule.getLastClass());

        return SubjectResponse.from(subjectRepository.save(dto.toEntity(schedule)));
    }

    @Transactional
    public SubjectResponse updateSubjects(final SubjectsUpdateRequest dto, final Long subjectsId,
                                          final Long userId) {

        User currentUser = userRepository.findUserById(userId);
        Subjects subjects = authorization(subjectsId, currentUser.getId());

        compareLastClass(dto.getClassTime(), subjects.getSchedule().getLastClass());

        updateEachSubjectsItem(dto, subjects);

        return SubjectResponse.from(subjects);
    }

    private void updateEachSubjectsItem(final SubjectsUpdateRequest dto, final Subjects subjects) {
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
    public SubjectsDeleteResponse deleteSubjects(final Long scheduleId, final Long subjectsId, final Long userId) {

        User currentUser = userRepository.findUserById(userId);
        Subjects subject = authorization(subjectsId, currentUser.getId());
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if (!subject.getSchedule().equals(schedule)) {
            throw new SubjectException(SCHEDULE_NOT_FOUND);
        }

        subjectRepository.deleteById(subject.getId());

        return SubjectsDeleteResponse.from(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getMyClass(final Long scheduleId, final ClassDay day, final Long userId) {
        matchUserWithSchedule(scheduleId, userId);

        return SubjectResponse.from(
                subjectQueryRepository.findAllByScheduleIdAndUserIdAndClassDay(scheduleId, userId, day));
    }


    @Transactional(readOnly = true)
    public SubjectDetailResponse getSubject(final Long scheduleId, final Long subjectId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);
        Subjects subject = authorization(subjectId, userId);

        return SubjectDetailResponse.from(subject);
    }

    private void matchUserWithSchedule(final Long scheduleId, final Long userId) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if (!schedule.getUser().equals(user)) {
            throw new SubjectException(SCHEDULE_NOT_FOUND);
        }
    }

    private Subjects authorization(final Long id, final Long userId) {

        Subjects subjects = subjectRepository.findSubjectsById(id);

        if (!subjects.getSchedule().getUser().getId().equals(userId)) {
            throw new SubjectException(USER_NOT_FOUND);
        }
        return subjects;

    }

    private int extractNumber(final String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new SubjectException(WRONG_CLASS_TIME);
    }

    private void compareLastClass(final String subjectLastClass, final String scheduleLastClass) {
        if (extractNumber(subjectLastClass) > extractNumber(scheduleLastClass)) {
            throw new SubjectException(WRONG_CLASS_TIME);
        }
    }
}
