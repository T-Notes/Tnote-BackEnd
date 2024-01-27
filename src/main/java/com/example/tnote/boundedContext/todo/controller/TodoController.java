package com.example.tnote.boundedContext.todo.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.service.TodoService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tnote/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Result> saveTodo(@RequestBody TodoRequestDto dto,
                                           @AuthenticationPrincipal PrincipalDetails user) {

        TodoResponseDto response = todoService.saveTodo(dto, user.getId());
        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{todoId}")
    public ResponseEntity<Result> updateSubjects(@RequestBody TodoUpdateRequestDto dto,
                                                 @PathVariable("todoId") Long todoId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        TodoResponseDto response = todoService.updateTodos(dto, todoId, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Result> deleteTodo(@PathVariable Long todoId,
                                             @AuthenticationPrincipal PrincipalDetails user) {

        TodoDeleteResponseDto response = todoService.deleteTodo(todoId, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 홈페이지에서 특정 날짜에 대한 todo list 조회 ( 날짜 안넘겨주면 오늘 날짜로 기본으로 매핑 )
    @GetMapping
    public ResponseEntity<Result> findTodo(
            @RequestParam(defaultValue = "1970-01-01", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal PrincipalDetails user) {

        if (date == null) {
            date = LocalDate.now();
        }

        List<TodoResponseDto> response = todoService.findAllTodos(date, user.getId());
        return ResponseEntity.ok(Result.of(response));
    }
}
