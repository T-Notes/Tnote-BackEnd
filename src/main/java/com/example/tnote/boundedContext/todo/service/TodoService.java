package com.example.tnote.boundedContext.todo.service;


import static com.example.tnote.base.exception.ErrorCode.DATA_NOT_FOUND;

import com.example.tnote.base.exception.CustomException;
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
    public TodoResponseDto saveTodo(TodoRequestDto dto, Long scheduleId, Long userId, LocalDate date) {

        matchUserWithSchedule(scheduleId, userId);
        Todo todo = dto.toEntity(checkCurrentUser(userId), checkSchedule(scheduleId), getLocalDate(date));

        return TodoResponseDto.of(todoRepository.save(todo));
    }

    @Transactional
    public TodoDeleteResponseDto deleteTodo(Long todoId, Long scheduleId, Long userId) {

        Todo todo = getTodo(scheduleId, todoId, userId);

        todoRepository.deleteById(todo.getId());
        return TodoDeleteResponseDto.of(todo);
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> findAllTodos(LocalDate date, Long scheduleId, Long userId) {

        checkSchedule(scheduleId);

        return TodoResponseDto.of(
                todoQueryRepository.findAllByUserIdAndDate(userId, scheduleId, getLocalDate(date)));
    }

    @Transactional
    public TodoResponseDto updateTodos(TodoUpdateRequestDto dto, Long scheduleId, Long todoId, Long userId,
                                       LocalDate date) {

        Todo todos = getTodo(scheduleId, todoId, userId);

        updateEachTodosItem(dto, todos, date);

        return TodoResponseDto.of(todos);
    }

    private void updateEachTodosItem(TodoUpdateRequestDto dto, Todo todos, LocalDate date) {

        if (date == null) {
            todos.updateDate(LocalDate.now());
        }
        if (dto.hasDate()) {
            todos.updateDate(dto.getDate());
        }
        if (dto.hasContent()) {
            todos.updateContent(dto.getContent());
        }
        if (dto.hasStatus() && dto.getStatus() != todos.getStatus()) {
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
            throw new CustomException(DATA_NOT_FOUND, "user 정보가 없습니다. ");
        }
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(DATA_NOT_FOUND, "user 정보가 없습니다. "));
    }

    private Schedule checkSchedule(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(DATA_NOT_FOUND, "학기 정보가 없습니다."));
    }


    private Todo authorization(Long id, User member) {

        Todo todos = todoRepository.findById(id).orElseThrow(
                () -> new CustomException(DATA_NOT_FOUND, "todo 정보가 없습니다. "));

        if (!todos.getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", todos.getUser());
            throw new CustomException(DATA_NOT_FOUND, "user 정보가 없습니다. ");
        }
        return todos;

    }

    @Transactional
    public TodoSliceResponseDto readTodosByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                LocalDate endDate, Pageable pageable) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay,
                endOfDay);
        Slice<Todo> allTodos = todoQueryRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay, endOfDay, pageable);

        int numberOfTodo = todos.size();
        List<TodoResponseDto> responseDto = allTodos.getContent().stream().map(TodoResponseDto::of).toList();

        return TodoSliceResponseDto.builder()
                .todos(responseDto)
                .numberOfTodo(numberOfTodo)
                .page(allTodos.getPageable().getPageNumber())
                .isLast(allTodos.isLast())
                .build();

    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> readDailyTodos(Long userId, Long scheduleId, LocalDate date) {

        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndDate(userId, scheduleId, date);

        return todos.stream().map(TodoResponseDto::of).toList();
    }

    @Transactional(readOnly = true)
    public List<TodoResponseDto> readMonthlyTodos(Long userId, Long scheduleId, LocalDate date) {
        List<Todo> todos = todoQueryRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return todos.stream().map(TodoResponseDto::of).toList();
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }
}
