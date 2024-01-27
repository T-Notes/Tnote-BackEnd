package com.example.tnote.utils;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
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
}
