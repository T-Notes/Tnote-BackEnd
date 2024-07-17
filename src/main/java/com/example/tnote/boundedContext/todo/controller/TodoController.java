package com.example.tnote.boundedContext.todo.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.todo.dto.TodoDeleteResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoRequestDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoUpdateRequestDto;
import com.example.tnote.boundedContext.todo.service.TodoService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/tnote/v1/todo")
@Tag(name = "Todo", description = "Todo API")
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/{scheduleId}")
    @Operation(summary = "create todo api", description = "accessToken로 Todo 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = TodoResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> saveTodo(@RequestBody TodoRequestDto dto,
                                           @PathVariable("scheduleId") Long scheduleId,
                                           @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                           @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        TodoResponseDto response = todoService.saveTodo(dto, scheduleId, currentUser.getId(), date);
        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{scheduleId}/{todoId}")
    @Operation(summary = "Todo api", description = "accessToken로 Todo 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = TodoResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> updateSubjects(@RequestBody TodoUpdateRequestDto dto,
                                                 @PathVariable Long scheduleId,
                                                 @PathVariable("todoId") Long todoId,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        TodoResponseDto response = todoService.updateTodos(dto, scheduleId, todoId, currentUser.getId(), date);

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}/{todoId}")
    @Operation(summary = "delete todo api", description = "accessToken로 Todo 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = TodoDeleteResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> deleteTodo(@PathVariable("todoId") Long todoId,
                                             @PathVariable("scheduleId") Long scheduleId,
                                             @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        TodoDeleteResponseDto response = todoService.deleteTodo(todoId, scheduleId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 홈페이지에서 특정 날짜에 대한 todo list 조회 ( 날짜 안넘겨주면 오늘 날짜로 기본으로 매핑 )
    @GetMapping("/{scheduleId}")
    @Operation(summary = "find Todo api", description = "accessToken로 Todo 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TodoResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findTodo(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        List<TodoResponseDto> response = todoService.findAllTodos(date, scheduleId, currentUser.getId());
        return ResponseEntity.ok(Result.of(response));
    }

}
