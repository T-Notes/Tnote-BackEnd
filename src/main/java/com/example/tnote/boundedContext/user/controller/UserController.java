package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.SchoolPlanUtil;
import com.example.tnote.base.utils.SchoolUtils;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.user.dto.UserAlarmUpdate;
import com.example.tnote.boundedContext.user.dto.UserDeleteResponse;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.AuthService;
import com.example.tnote.boundedContext.user.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
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
@RequestMapping("/tnote/v1/user")
@Tag(name = "User", description = "User API")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final SchoolUtils schoolUtils;
    private final SchoolPlanUtil schoolPlanUtil;

    @GetMapping("/school")
    @Operation(summary = "search school info API", description = "학교 기본 정보 검색 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findSchool(@RequestParam("region") final String region,
                                             @RequestParam("code") final String code,
                                             @RequestParam("schoolType") final String schoolType,
                                             @RequestParam("schoolName") final String schoolName)
            throws IOException, net.minidev.json.parser.ParseException {

        String apiUrl = schoolPlanUtil.buildApiUrl(region, schoolType, schoolName);
        String apiResponse = schoolPlanUtil.fetchApiData(apiUrl);

        JSONObject parsingData = (JSONObject) new JSONParser().parse(apiResponse);

        JSONObject dataSearch = (JSONObject) parsingData.get("dataSearch");
        JSONArray content = (JSONArray) dataSearch.get("content");

        ArrayList<Object> schoolList = new ArrayList<>();

        // 데이터 출력하기
        JSONObject tmp;
        for (int i = 0; i < content.size(); i++) {
            tmp = (JSONObject) content.get(i);
            schoolList.add(Arrays.asList(tmp.get("schoolName"), tmp.get("adres")));
        }

        String info = schoolUtils.schoolInfo("schoolInfo", code, schoolName, schoolType);

        schoolList.add(schoolPlanUtil.extractSchoolCode(info));

        return ResponseEntity.ok(Result.of(schoolList));
    }

    @GetMapping("/school/plan")
    @Operation(summary = "search school plan info API", description = "학사 일정 정보 검색 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findSchoolPlan(@RequestParam("code") final String code,
                                                 @RequestParam("scheduleCode") final String scheduleCode)
            throws IOException {

        String result = schoolPlanUtil.schoolPlan("SchoolSchedule", code, scheduleCode);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(result);
        JsonNode rowNode = rootNode.path("SchoolSchedule").get(1).path("row");

        // 특정 필드를 추출하여 리스트로 저장
        List<String> resultList = new ArrayList<>();

        for (JsonNode node : rowNode) {
            String AA_YMD = node.path("AA_YMD").asText(); // 학사 일자
            String EVENT_NM = node.path("EVENT_NM").asText(); // 행사 명
            resultList.add(
                    String.format("AA_YMD: %s, EVENT_NM: %s", AA_YMD, EVENT_NM));
        }

        return ResponseEntity.ok(Result.of(resultList));

    }


    @GetMapping
    @Operation(summary = "find user info API(with accessToken)", description = "accessToken로 User 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> getInfo(@AuthenticationPrincipal final PrincipalDetails user) {
        UserResponse response = userService.findById(user.getId());
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "find user info api(with userId)", description = "userId로 User 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> getUserInfo(
            @Parameter(required = true, description = "userId 작성") @PathVariable final Long userId) {

        UserResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok(Result.of(response));
    }


    @PatchMapping
    @Operation(summary = "update user info api", description = "accessToken로 User 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> updateExtraInfo(@AuthenticationPrincipal final PrincipalDetails user,
                                                  @RequestBody final UserUpdateRequest dto) {

        UserResponse response = userService.updateExtraInfo(user.getId(), dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/alarm")
    @Operation(summary = "update user alarm info api", description = "accessToken로 User alarm 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> updateAlarmInfo(@AuthenticationPrincipal final PrincipalDetails user,
                                                  @RequestBody final UserAlarmUpdate dto) {

        UserResponse response = userService.updateAlarmInfo(user.getId(), dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "logout api", description = "로그아웃 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> logout(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @AuthenticationPrincipal final PrincipalDetails user) {

        TokenUtils.checkValidToken(user);

        userService.logout(request, response);

        return ResponseEntity.ok(Result.of("로그아웃 되었습니다."));
    }

    @DeleteMapping
    @Operation(summary = "delete user info api", description = "accessToken로 User 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserDeleteResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> deleteUser(@AuthenticationPrincipal final PrincipalDetails user
            , HttpServletRequest request) {
        String oauthRefreshToken = request.getHeader("oauthRefreshToken");

        UserDeleteResponse response = authService.deleteUser(user.getId(), oauthRefreshToken);

        return ResponseEntity.ok(Result.of(response));
    }

    // 탈퇴할때 작성할 회원의 메일 조회 - depreciated
    @GetMapping("/mail")
    @Operation(summary = "find user mail api", description = "User 메일 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UserMailResponse.class))}),
            @ApiResponse(responseCode = "404", description = "실패")
    })
    public ResponseEntity<Result> getMail(@AuthenticationPrincipal final PrincipalDetails user) {

        UserMailResponse response = userService.getMail(user.getId());

        return ResponseEntity.ok(Result.of(response));
    }
}