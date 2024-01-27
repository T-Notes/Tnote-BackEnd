package com.example.tnote.boundedContext.todo.service;

import com.example.tnote.base.exception.todo.TodoErrorResult;
import com.example.tnote.base.exception.todo.TodoException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoQueryRepository;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
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

    @Transactional
    public TodoResponseDto saveTodo(TodoRequestDto dto, Long userId) {

        User currentUser = checkCurrentUser(userId);

        Todo todo = dto.toEntity(currentUser);

        return TodoResponseDto.of(todoRepository.save(todo));
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
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

    @Transactional
    public TodoDeleteResponseDto deleteTodo(Long todoId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Todo todo = authorization(todoId, currentUser);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.builder()
                .id(todo.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(LocalDate date, Long userId) {

        return TodoResponseDto.of(
                todoQueryRepository.findAllByUserIdAndDate(userId, date));
    }

    @Transactional
    public TodoResponseDto updateTodos(TodoUpdateRequestDto dto, Long todoId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Todo todos = authorization(todoId, currentUser);

        updateEachTodosItem(dto, todos);

        return TodoResponseDto.of(todos);
    }

    private void updateEachTodosItem(TodoUpdateRequestDto dto, Todo todos) {
        if (dto.hasDate()) {
            todos.updateDate(dto.getDate());
        }
        if (dto.hasContent()) {
            todos.updateContent(dto.getContent());
        }
    }
}
