package com.example.tnote.utils;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.repository.SubjectRepository;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestSyUtils {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final ScheduleRepository scheduleRepository;
    private final SubjectRepository subjectRepository;
    private final ConsultationRepository consultationRepository;
    private final ClassLogRepository classLogRepository;
    private final ProceedingRepository proceedingRepository;
    private final ObservationRepository observationRepository;

    public void login(PrincipalDetails user) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities()));
    }

    public User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .username(name)
                .build();

        return userRepository.save(user);
    }

    public User createUser(String email, String name, String schoolName, String subject, int career, boolean alarm) {
        User user = User.builder()
                .email(email)
                .username(name)
                .school(schoolName)
                .career(career)
                .alarm(alarm)
                .subject(subject)
                .build();

        return userRepository.save(user);
    }

    public Todo createTodo(String content, LocalDate date, Boolean status, User user, Schedule schedule) {
        Todo todo = Todo.builder()
                .content(content)
                .date(date)
                .status(status)
                .user(user)
                .schedule(schedule)
                .build();

        return todoRepository.save(todo);
    }

    public Schedule createSchedule(String semesterName, String lastClass, User user, LocalDate startDate,
                                   LocalDate endDate) {
        Schedule schedule = Schedule.builder()
                .semesterName(semesterName)
                .lastClass(lastClass)
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return scheduleRepository.save(schedule);
    }

    public Schedule createSchedule(String semesterName, String lastClass, User user, LocalDate startDate,
                                   LocalDate endDate, List<Subjects> subjects) {

        Schedule saved = new Schedule();

        for (Subjects data : subjects) {

            Subjects subjectsList = createSubjects(data.getSubjectName(), data.getClassTime(), data.getClassDay()
                    , data.getClassLocation(), data.getMemo(), data.getColor(), data.getSchedule());

            Schedule schedule = Schedule.builder()
                    .subjectsList(Collections.singletonList(subjectsList))
                    .semesterName(semesterName)
                    .lastClass(lastClass)
                    .user(user)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            saved = scheduleRepository.save(schedule);
        }
        return saved;

    }

    public Subjects createSubjects(String subjectName, String classTime, ClassDay classDay,
                                   String classLocation, String memo, String color, Schedule schedule) {
        Subjects subjects = Subjects.builder()
                .subjectName(subjectName)
                .classTime(classTime)
                .classDay(classDay)
                .classLocation(classLocation)
                .memo(memo)
                .color(color)
                .schedule(schedule)
                .build();

        return subjectRepository.save(subjects);
    }

    public Consultation createConsultation(String studentName, LocalDateTime startDate, LocalDateTime endDate,
                                           CounselingField counselingField, CounselingType counselingType,
                                           String consultationContents, String consultationResult) {
        Consultation consultation = Consultation.builder()
                .title(studentName)
                .startDate(startDate)
                .endDate(endDate)
                .counselingField(counselingField)
                .counselingType(counselingType)
                .consultationContents(consultationContents)
                .consultationResult(consultationResult)
                .build();

        return consultationRepository.save(consultation);
    }

    public Proceeding createProceeding(String title, LocalDateTime startDate, LocalDateTime endDate,
                                       String location, String workContents) {
        Proceeding consultation = Proceeding.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .location(location)
                .workContents(workContents)
                .build();

        return proceedingRepository.save(consultation);
    }

    public Observation createObservation(String studentName, LocalDateTime startDate, LocalDateTime endDate,
                                         String observationContents, String guidance) {
        Observation consultation = Observation.builder()
                .title(studentName)
                .startDate(startDate)
                .endDate(endDate)
                .observationContents(observationContents)
                .guidance(guidance)
                .build();

        return observationRepository.save(consultation);
    }
}
