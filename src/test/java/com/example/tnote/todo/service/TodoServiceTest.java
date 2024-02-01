package com.example.tnote.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.todo.TodoException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.service.TodoService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class TodoServiceTest {
    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    TodoService todoService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    PrincipalDetails principalDetails;
    Todo todo1;
    Todo todo2;
    Schedule schedule1;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);

        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());

        schedule1 = testSyUtils.createSchedule("test1", "9교시", user1, LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-06-01"));

        todo1 = testSyUtils.createTodo("test1", LocalDate.parse("2024-01-27"), user1);
        todo2 = testSyUtils.createTodo("test12", LocalDate.parse("2024-01-27"), user1);
    }

    @Test
    @DisplayName("todo 작성 성공")
    void saveTodo() {

        // given
        testSyUtils.login(principalDetails);

        TodoRequestDto dto = TodoRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // when
        Todo todo = dto.toEntity(user1);

        // then
        assertThat(todo.getDate()).isEqualTo(LocalDate.parse("2024-01-27"));
        assertThat(todo.getContent()).isEqualTo("test1");
    }

    @Test
    @DisplayName("로그인 하지 않은 user todo 작성 실패")
    void notLoginSaveTodo() {

        // given

        TodoRequestDto dto = TodoRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // when

        // then
        assertThatThrownBy(() -> todoService.saveTodo(dto, schedule1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("없는 학기 todo 작성 실패")
    void notExistScheduleSaveTodo() {

        // given
        testSyUtils.login(principalDetails);

        TodoRequestDto dto = TodoRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // when

        // then
        assertThatThrownBy(() -> todoService.saveTodo(dto, 222L, user1.getId()))
                .isInstanceOf(ScheduleException.class);
    }

    @Test
    @DisplayName("다른 유저 todo 작성 실패")
    void otherUserSaveTodo() {

        // given
        testSyUtils.login(principalDetails);

        TodoRequestDto dto = TodoRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // when

        // then
        assertThatThrownBy(() -> todoService.saveTodo(dto, schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }


    @Test
    @DisplayName("todo 삭제 성공")
    void deleteTodo() {

        // given
        testSyUtils.login(principalDetails);

        // when
        todoService.deleteTodo(todo1.getId(), schedule1.getId(), user1.getId());

        // then
        assertThatThrownBy(() -> todoService.deleteTodo(todo1.getId(), schedule1.getId(), user1.getId()))
                .isInstanceOf(TodoException.class);
    }

    @Test
    @DisplayName("다른 유저 todo 삭제 실패")
    void otherUserDeleteTodo() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> todoService.deleteTodo(todo1.getId(), schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("로그인 하지 않은 유저 todo 삭제 실패")
    void notLoginDeleteTodo() {

        // given

        // when

        // then
        assertThatThrownBy(() -> todoService.deleteTodo(todo1.getId(), schedule1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("존재하지 않은 todo 삭제 실패")
    void notExistDeleteTodo() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> todoService.deleteTodo(222L, schedule1.getId(), user1.getId()))
                .isInstanceOf(TodoException.class);
    }

    @Test
    @DisplayName("없는 학기 todo 삭제 실패")
    void notExistScheduleDeleteTodo() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> todoService.deleteTodo(todo1.getId(), 222L, user1.getId()))
                .isInstanceOf(ScheduleException.class);
    }


    @Test
    @DisplayName("todo 날짜별 전체 조회 성공")
    void findAllTodos() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<TodoResponseDto> todos = todoService.findAllTodos(LocalDate.parse("2024-01-27"), user1.getId());

        // then
        assertThat(todos.get(0).getDate()).isEqualTo(LocalDate.parse("2024-01-27"));
        assertThat(todos.get(0).getContent()).isEqualTo("test1");
        assertThat(todos.get(1).getDate()).isEqualTo(LocalDate.parse("2024-01-27"));
        assertThat(todos.get(1).getContent()).isEqualTo("test12");
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 todo 날짜별 전체 조회 실패")
    void notLoginFindAllTodos() {

        // given

        // when

        // then
        assertThatThrownBy(() -> todoService.findAllTodos(LocalDate.parse("2024-01-27"), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("todo 수정 성공")
    void updateTodos() {

        // given
        testSyUtils.login(principalDetails);

        // when

        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        TodoResponseDto response = todoService.updateTodos(dto, schedule1.getId(), todo1.getId(), user1.getId());

        // then
        assertThat(response.getContent()).isEqualTo(dto.getContent());
        assertThat(response.getDate()).isEqualTo(dto.getDate());
    }

    @Test
    @DisplayName("존재하지 않은 todo 수정 실패")
    void notExistUpdateTodos() {

        // given
        testSyUtils.login(principalDetails);

        // when

        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // then
        assertThatThrownBy(() -> todoService.updateTodos(dto, schedule1.getId(), 222L, user1.getId()))
                .isInstanceOf(TodoException.class);
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 todo 수정 실패")
    void notLoginUpdateTodos() {

        // given

        // when

        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // then
        assertThatThrownBy(() -> todoService.updateTodos(dto, schedule1.getId(), todo1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("다른 유저의 todo 수정 실패")
    void otherUserUpdateTodos() {

        // given
        testSyUtils.login(principalDetails);

        // when

        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // then
        assertThatThrownBy(() -> todoService.updateTodos(dto, schedule1.getId(), todo1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("없는 학기의 todo 수정 실패")
    void notExistScheduleUpdateTodos() {

        // given
        testSyUtils.login(principalDetails);

        // when

        TodoUpdateRequestDto dto = TodoUpdateRequestDto.builder()
                .date(LocalDate.parse("2024-01-27"))
                .content("test1")
                .build();

        // then
        assertThatThrownBy(() -> todoService.updateTodos(dto, 222L, todo1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }
}
