package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.dto.UserRequest;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Value("${api.career-key}")
    private String KEY;

    @Value("${api.call-back-url}")
    private String callBackUrl;

    @GetMapping("/school")
    public ResponseEntity<Result> findSchool(@RequestBody UserRequest dto) throws IOException, ParseException {

        log.info("api 사용해서 특정 학교 찾기");

        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String result = null;

        // encoding , api param에 맞게 custom
        String gubun = userService.changeGubun(dto.getGubun());
        int encodeRegion = userService.findCityCode(dto.getRegion());
        String encodeSchoolName = URLEncoder.encode(dto.getSchoolName(),"UTF-8");


        StringBuilder urlStr = new StringBuilder(
                callBackUrl + "apiKey=" + KEY
                        + "&svcType=api&svcCode=SCHOOL&contentType=json"
                        + "&gubun=" + gubun
                        + "&region=" + encodeRegion
                        + "&searchSchulNm=" + encodeSchoolName);
        try {
            URL url = new URL(urlStr.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            stream = userService.getNetworkConnection(urlConnection);
            result = userService.readStreamToString(stream);

            if (stream != null) stream.close();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        JSONObject parsingData = (JSONObject) new JSONParser().parse(result.toString());

        // REST API 호출 상태 출력하기
        StringBuilder out = new StringBuilder();
        out.append(parsingData.get("status") +" : " + parsingData.get("status_message") +"\n");

        JSONObject dataSearch = (JSONObject) parsingData.get("dataSearch");
        JSONArray content = (JSONArray) dataSearch.get("content");

        ArrayList<Object> schoolList = new ArrayList<>();

        // 데이터 출력하기
        JSONObject tmp;
        for(int i=0; i<content.size(); i++) {
            tmp = (JSONObject) content.get(i);
            schoolList.add(Arrays.asList(tmp.get("schoolName"), tmp.get("adres")));
        }

        return ResponseEntity.ok(Result.of(schoolList));
    }



    @PatchMapping("/{userId}")
    public ResponseEntity<Result> updateExtraInfo(@PathVariable Long userId, @RequestBody UserRequest dto) throws IOException {
        log.info(" user controller - user 추가 정보 등록 / 수정 같은 api 사용, alarm 수신 여부만 바꿔도 여기서 처리");

        UserResponse response = userService.updateExtraInfo(userId, dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Result> logout(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @AuthenticationPrincipal PrincipalDetails user) {

        log.info("PrincipalDetails in user - controller / logout : {}", user);

        if (user == null) {
            log.warn("PrincipalDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }

        userService.logout(request, response, user);

        return ResponseEntity.ok(Result.of("로그아웃 되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<Result> deleteUser(@AuthenticationPrincipal PrincipalDetails user) {

        log.info("PrincipalDetails in user - controller / delete user : {}", user);

        if (user == null) {
            log.warn("PrincipalDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }

        userService.deleteUser(user);

        return ResponseEntity.ok(Result.of("탈퇴 처리가 완료 되었습니다."));
    }
}
