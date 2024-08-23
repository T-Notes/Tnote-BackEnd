package com.example.tnote.boundedContext.todo.service;


import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponse;
import com.example.tnote.boundedContext.todo.dto.TodoRequest;
import com.example.tnote.boundedContext.todo.dto.TodoResponse;
import com.example.tnote.boundedContext.todo.dto.TodoSliceResponse;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequest;
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
    public TodoResponse saveTodo(final TodoRequest dto, final Long scheduleId, final Long userId,
                                 final LocalDate date) {

        matchUserWithSchedule(scheduleId, userId);

        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        Todo todo = dto.toEntity(user, schedule, getLocalDate(date));

        return TodoResponse.from(todoRepository.save(todo));
    }

    @Transactional
    public TodoDeleteResponse deleteTodo(final Long todoId, final Long scheduleId, final Long userId) {

        Todo todo = getTodo(scheduleId, todoId, userId);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponse.from(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findAllTodos(final LocalDate date, final Long scheduleId, final Long userId) {

        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        return TodoResponse.from(
                todoQueryRepository.findAllByUserIdAndDate(userId, schedule.getId(), getLocalDate(date)));
    }

    @Transactional
    public TodoResponse updateTodos(final TodoUpdateRequest dto, final Long scheduleId, final Long todoId,
                                    final Long userId,
                                    final LocalDate date) {

        Todo todos = getTodo(scheduleId, todoId, userId);

        updateEachTodosItem(dto, todos, date);

        return TodoResponse.from(todos);
    }

    private void updateEachTodosItem(final TodoUpdateRequest dto, final Todo todos, final LocalDate date) {

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
        User currentUser = userRepository.findUserById(userId);

        matchUserWithSchedule(scheduleId, currentUser.getId());
        return authorization(todoId, currentUser);
    }

    private void matchUserWithSchedule(final Long scheduleId, final Long userId) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        User currentUser = userRepository.findUserById(userId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("학기를 작성한 user와 현 user가 다릅니다");
            throw new TodoException(USER_NOT_FOUND);
        }
    }

    private Todo authorization(final Long id, final User member) {

        Todo todos = todoRepository.findTodoById(id);

        if (!todos.getUser().getId().equals(member.getId())) {
            throw new TodoException(USER_NOT_FOUND);
        }
        return todos;

    }

    @Transactional
    public TodoSliceResponse readTodosByDate(final Long userId, final Long scheduleId, final LocalDate startDate,
                                             final LocalDate endDate, Pageable pageable) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay,
                endOfDay);
        Slice<Todo> allTodos = todoQueryRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay, endOfDay, pageable);

        int numberOfTodo = todos.size();
        List<TodoResponse> responseDto = allTodos.getContent().stream().map(TodoResponse::from).toList();

        return TodoSliceResponse.builder()
                .todos(responseDto)
                .numberOfTodo(numberOfTodo)
                .page(allTodos.getPageable().getPageNumber())
                .isLast(allTodos.isLast())
                .build();

    }

    @Transactional(readOnly = true)
    public List<TodoResponse> readDailyTodos(final Long userId, final Long scheduleId, final LocalDate date) {

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndDate(userId, scheduleId, date);

        return todos.stream().map(TodoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> readMonthlyTodos(final Long userId, final Long scheduleId, final LocalDate date) {
        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return todos.stream().map(TodoResponse::from).toList();
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }
}
