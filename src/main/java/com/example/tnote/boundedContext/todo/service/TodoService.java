package com.example.tnote.boundedContext.todo.service;

import com.example.tnote.base.exception.schedule.ScheduleErrorResult;
import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.todo.TodoErrorResult;
import com.example.tnote.base.exception.todo.TodoException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoSliceResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.repository.TodoQueryRepository;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final TodoRepository todoRepository;
    private final TodoQueryRepository todoQueryRepository;

    @Transactional
    public TodoResponseDto saveTodo(TodoRequestDto dto, Long scheduleId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);
        Todo todo = dto.toEntity(checkCurrentUser(userId), checkSchedule(scheduleId));

        return TodoResponseDto.of(todoRepository.save(todo));
    }

    @Transactional
    public TodoDeleteResponseDto deleteTodo(Long todoId, Long scheduleId, Long userId) {

        Todo todo = getTodo(scheduleId, todoId, userId);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.builder()
                .id(todo.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(LocalDate date, Long scheduleId, Long userId) {

        checkSchedule(scheduleId);

        return TodoResponseDto.of(
                todoQueryRepository.findAllByUserIdAndDate(userId, scheduleId, date));
    }

    @Transactional
    public TodoResponseDto updateTodos(TodoUpdateRequestDto dto, Long scheduleId, Long todoId, Long userId) {

        Todo todos = getTodo(scheduleId, todoId, userId);

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
        if (dto.hasStatus()) {
            todos.updateStatus(dto.getStatus());
        }
    }

    private Todo getTodo(Long scheduleId, Long todoId, Long userId) {
        User currentUser = checkCurrentUser(userId);

        matchUserWithSchedule(scheduleId, currentUser.getId());
        Todo todos = authorization(todoId, currentUser);
        return todos;
    }

    private void matchUserWithSchedule(Long scheduleId, Long userId) {
        Schedule schedule = checkSchedule(scheduleId);
        User currentUser = checkCurrentUser(userId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("학기를 작성한 user와 현 user가 다릅니다");
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    private Schedule checkSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorResult.SCHEDULE_NOT_FOUND));
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

    public TodoSliceResponseDto readTodosByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Todo> todos = todoRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId, startOfDay,
                endOfDay);
        Slice<Todo> allTodos = todoRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(userId, scheduleId,
                startOfDay, endOfDay, pageable);

        int numberOfTodo = todos.size();
        List<TodoResponseDto> responseDto = allTodos.getContent().stream().map(TodoResponseDto::of).toList();

        return TodoSliceResponseDto.builder()
                .todos(responseDto)
                .numberOfTodo(numberOfTodo)
                .page(allTodos.getPageable().getPageNumber())
                .isLast(allTodos.isLast())
                .build();

    }

    public TodoSliceResponseDto readDailyTodos(Long userId, Long scheduleId,
                                               LocalDate date, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);
        List<Todo> todos = todoRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId, startOfDay,
                endOfDay);
        Slice<Todo> allTodos = todoRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(userId, scheduleId,
                startOfDay, endOfDay, pageable);

        int numberOfTodo = todos.size();
        List<TodoResponseDto> responseDto = allTodos.getContent().stream().map(TodoResponseDto::of).toList();

        return TodoSliceResponseDto.builder()
                .todos(responseDto)
                .numberOfTodo(numberOfTodo)
                .page(allTodos.getPageable().getPageNumber())
                .isLast(allTodos.isLast())
                .build();

    }
}
