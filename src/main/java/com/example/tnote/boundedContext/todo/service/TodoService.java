package com.example.tnote.boundedContext.todo.service;

import com.example.tnote.base.exception.TodoErrorResult;
import com.example.tnote.base.exception.TodoException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
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
    private final ScheduleRepository scheduleRepository;
    private final TodoQueryRepository todoQueryRepository;

    public TodoResponseDto saveTodo(TodoRequestDto dto, Long scheduleId, PrincipalDetails user) {

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        User currentUser = checkCurrentUser(user.getId());

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("스케쥴을 작성한 user와 현재 todo를 작성하려는 user가 다르다 currentUser : {}", currentUser);
            throw new UserException(UserErrorResult.WRONG_USRE);
        }

        Todo todo = Todo.builder()
                .date(dto.getDate())
                .content(dto.getContent())
                .schedule(schedule)
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

        if (!todos.getSchedule().getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", todos.getSchedule().getUser());
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
        return todos;

    }

    public TodoDeleteResponseDto deleteTodo(Long scheduleId, Long todoId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Todo todo = authorization(todoId, currentUser);
        log.info("todo : {}", todo);
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        log.info("schedule user : {}", schedule.getUser().getId());

        if (!todo.getSchedule().equals(schedule)) {
            log.warn("해당하는 학기가 존재하지 않습니다");
            throw new TodoException(TodoErrorResult.SCHEDULE_NOT_FOUND);
        }

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.builder()
                .id(todo.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(Long scheduleId, LocalDate date, PrincipalDetails user) {

        log.info("use id : {}, date : {}", user.getId(), date);

        return TodoResponseDto.of(
                todoQueryRepository.findAllByUserIdAndScheduleIdAndDate(user.getId(), scheduleId, date));
    }
}
