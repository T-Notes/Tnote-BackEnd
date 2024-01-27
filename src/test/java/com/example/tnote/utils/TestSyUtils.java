package com.example.tnote.utils;

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

    public Todo createTodo(String content, LocalDate date, User user) {
        Todo todo = Todo.builder()
                .content(content)
                .date(date)
                .user(user)
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
                    , data.getClassLocation(), data.getMemo(), data.getColor(), data.getDate(), data.getSchedule());

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
                                   String classLocation, String memo, String color, LocalDate date,
                                   Schedule schedule) {
        Subjects subjects = Subjects.builder()
                .subjectName(subjectName)
                .classTime(classTime)
                .classDay(classDay)
                .classLocation(classLocation)
                .memo(memo)
                .color(color)
                .date(date)
                .schedule(schedule)
                .build();

        return subjectRepository.save(subjects);
    }
}
