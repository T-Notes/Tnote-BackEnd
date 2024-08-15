package com.example.tnote.boundedContext.todo.service;


import static com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode.SCHEDULE_NOT_FOUND;
import static com.example.tnote.boundedContext.todo.exception.TodoErrorCode.TODO_NOT_FOUND;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoSliceResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.exception.TodoException;
import com.example.tnote.boundedContext.todo.repository.TodoQueryRepository;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    public TodoResponseDto saveTodo(final TodoRequestDto dto, final Long scheduleId, final Long userId,
                                    final LocalDate date) {

        matchUserWithSchedule(scheduleId, userId);
        Todo todo = dto.toEntity(checkCurrentUser(userId), checkSchedule(scheduleId), getLocalDate(date));

        return TodoResponseDto.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoDeleteResponseDto deleteTodo(final Long todoId, final Long scheduleId, final Long userId) {

        Todo todo = getTodo(scheduleId, todoId, userId);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.from(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(final LocalDate date, final Long scheduleId, final Long userId) {

        checkSchedule(scheduleId);

        return TodoResponseDto.from(
                todoQueryRepository.findAllByUserIdAndDate(userId, scheduleId, getLocalDate(date)));
    }

    @Transactional
    public TodoResponseDto updateTodos(final TodoUpdateRequestDto dto, final Long scheduleId, final Long todoId,
                                       final Long userId,
                                       final LocalDate date) {

        Todo todos = getTodo(scheduleId, todoId, userId);

        updateEachTodosItem(dto, todos, date);

        return TodoResponseDto.from(todos);
    }

    private void updateEachTodosItem(final TodoUpdateRequestDto dto, final Todo todos, final LocalDate date) {

        if (date == null) {
            todos.updateDate(LocalDate.now());
        }
        if (dto.hasDate()) {
            todos.updateDate(dto.getDate());
        }
        if (dto.hasContent()) {
            todos.updateContent(dto.getContent());
        }
        if (dto.hasStatus() && !Objects.equals(dto.getStatus(), todos.getStatus())) {
            todos.updateStatus(dto.getStatus());
        }
    }

    private Todo getTodo(final Long scheduleId, final Long todoId, final Long userId) {
        User currentUser = checkCurrentUser(userId);

        matchUserWithSchedule(scheduleId, currentUser.getId());
        return authorization(todoId, currentUser);
    }

    private void matchUserWithSchedule(final Long scheduleId, final Long userId) {
        Schedule schedule = checkSchedule(scheduleId);
        User currentUser = checkCurrentUser(userId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("학기를 작성한 user와 현 user가 다릅니다");
            throw new TodoException(USER_NOT_FOUND);
        }
    }

    private User checkCurrentUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new TodoException(USER_NOT_FOUND));
    }

    private Schedule checkSchedule(final Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new TodoException(SCHEDULE_NOT_FOUND));
    }


    private Todo authorization(final Long id, final User member) {

        Todo todos = todoRepository.findById(id).orElseThrow(
                () -> new TodoException(TODO_NOT_FOUND));

        if (!todos.getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", todos.getUser());
            throw new TodoException(USER_NOT_FOUND);
        }
        return todos;

    }

    @Transactional
    public TodoSliceResponseDto readTodosByDate(final Long userId, final Long scheduleId, final LocalDate startDate,
                                                final LocalDate endDate, Pageable pageable) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay,
                endOfDay);
        Slice<Todo> allTodos = todoQueryRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay, endOfDay, pageable);

        int numberOfTodo = todos.size();
        List<TodoResponseDto> responseDto = allTodos.getContent().stream().map(TodoResponseDto::from).toList();

        return TodoSliceResponseDto.builder()
                .todos(responseDto)
                .numberOfTodo(numberOfTodo)
                .page(allTodos.getPageable().getPageNumber())
                .isLast(allTodos.isLast())
                .build();

    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> readDailyTodos(final Long userId, final Long scheduleId, final LocalDate date) {

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndDate(userId, scheduleId, date);

        return todos.stream().map(TodoResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> readMonthlyTodos(final Long userId, final Long scheduleId, final LocalDate date) {
        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return todos.stream().map(TodoResponseDto::from).toList();
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }
}
