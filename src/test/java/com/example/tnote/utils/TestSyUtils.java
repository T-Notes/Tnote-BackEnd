package com.example.tnote.utils;

import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.dto.UserRequest;
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

    public UserRequest createUserRequest(String email, String name) {
        return createUserRequest(email, name, null, null, 0, false);
    }

    public UserRequest createUserRequest(String email, String name, String schoolName, String subject, int career,
                                         boolean alarm) {
        return UserRequest.builder()
                .email(email)
                .name(name)
                .schoolName(schoolName)
                .subject(subject)
                .career(career)
                .alarm(alarm)
                .build();
    }
}
