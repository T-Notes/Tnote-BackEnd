package com.example.tnote.boundedContext.todo.service;

import com.example.tnote.base.exception.TodoErrorResult;
import com.example.tnote.base.exception.TodoException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoQueryRepository;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
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
public class TodoService {

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;

    public TodoResponseDto saveTodo(TodoRequestDto dto, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());

        Todo todo = Todo.builder()
                .date(dto.getDate())
                .content(dto.getContent())
                .user(currentUser)
                .build();

        return TodoResponseDto.of(todoRepository.save(todo));
    }

    private User checkCurrentUser(Long id) {
        Optional<User> currentUser = userRepository.findById(id);

        if (currentUser.isEmpty()) {
            log.warn("해당하는 유저가 없습니다. currentUser : {}", currentUser);
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        return currentUser.get();
    }

    private Todo authorization(Long id, User member) {

        Todo todos = todoRepository.findById(id).orElseThrow(
                () -> new TodoException(TodoErrorResult.TODO_NOT_FOUND));

        if (!todos.getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", todos.getUser());
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
        return todos;

    }

    public TodoDeleteResponseDto deleteTodo(Long todoId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Todo todo = authorization(todoId, currentUser);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.builder()
                .id(todo.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(LocalDate date, PrincipalDetails user) {

        return TodoResponseDto.of(
                todoQueryRepository.findAllByUserIdAndDate(user.getId(), date));
    }
}
