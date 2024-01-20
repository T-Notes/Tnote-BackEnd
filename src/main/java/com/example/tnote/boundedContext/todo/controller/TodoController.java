package com.example.tnote.boundedContext.todo.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Result> saveTodo(@RequestBody TodoRequestDto dto,
                                           @AuthenticationPrincipal PrincipalDetails user) {

        TodoResponseDto response = todoService.saveTodo(dto, user);
        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Result> deleteTodo(@PathVariable Long todoId,
                                             @AuthenticationPrincipal PrincipalDetails user) {

        TodoDeleteResponseDto response = todoService.deleteTodo(todoId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    // 홈페이지에서 "오늘" 버튼 및 특정 요일 눌렀을때 옆에 나오게끔 한다.
    @GetMapping
    public ResponseEntity<Result> findTodo(
            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal PrincipalDetails user) {

        List<TodoResponseDto> response = todoService.findAllTodos(date, user);
        return ResponseEntity.ok(Result.of(response));
    }
}
