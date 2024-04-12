package com.example.tnote.boundedContext.user.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.FindCityUtils;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.user.dto.UserDeleteResponseDto;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/tnote/user")
public class UserController {

    private final UserService userService;
    private final FindCityUtils findCityUtils;

    @Value("${api.career-key}")
    private String KEY;

    @Value("${api.call-back-url}")
    private String callBackUrl;


    @GetMapping("/school")
    public ResponseEntity<Result> findSchool(@RequestParam("region") String region,
                                             @RequestParam("schoolType") String schoolType,
                                             @RequestParam("schoolName") String schoolName)
            throws IOException, ParseException {

        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String result = null;

        String gubun = findCityUtils.changeGubun(schoolType);
        int encodeRegion = findCityUtils.findCityCode(region);
        String encodeSchoolName = URLEncoder.encode(schoolName, "UTF-8");

        StringBuilder urlStr = new StringBuilder(
                callBackUrl + "apiKey=" + KEY
                        + "&svcType=api&svcCode=SCHOOL&contentType=json"
                        + "&gubun=" + gubun
                        + "&region=" + encodeRegion
                        + "&searchSchulNm=" + encodeSchoolName);
        try {
            URL url = new URL(urlStr.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            stream = findCityUtils.getNetworkConnection(urlConnection);
            result = findCityUtils.readStreamToString(stream);

            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        JSONObject parsingData = (JSONObject) new JSONParser().parse(result.toString());

        // REST API 호출 상태 출력하기
        StringBuilder out = new StringBuilder();
        out.append(parsingData.get("status") + " : " + parsingData.get("status_message") + "\n");

        JSONObject dataSearch = (JSONObject) parsingData.get("dataSearch");
        JSONArray content = (JSONArray) dataSearch.get("content");

        ArrayList<Object> schoolList = new ArrayList<>();

        // 데이터 출력하기
        JSONObject tmp;
        for (int i = 0; i < content.size(); i++) {
            tmp = (JSONObject) content.get(i);
            schoolList.add(Arrays.asList(tmp.get("schoolName"), tmp.get("adres")));
        }

        return ResponseEntity.ok(Result.of(schoolList));
    }

    @GetMapping
    public ResponseEntity<Result> getInfo(@AuthenticationPrincipal PrincipalDetails user) {
        UserResponse response = UserResponse.of(userService.findById(user.getId()));
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Result> getUserInfo(@PathVariable Long userId) {

        UserResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok(Result.of(response));
    }


    @PatchMapping
    public ResponseEntity<Result> updateExtraInfo(@AuthenticationPrincipal PrincipalDetails user,
                                                  @RequestBody UserUpdateRequest dto) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        UserResponse response = userService.updateExtraInfo(currentUser.getId(), dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Result> logout(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        userService.logout(request, response, currentUser.getId());

        return ResponseEntity.ok(Result.of("로그아웃 되었습니다."));
    }

    @DeleteMapping
    public ResponseEntity<Result> deleteUser(@AuthenticationPrincipal PrincipalDetails user
            , @RequestParam String code) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        UserDeleteResponseDto response = userService.deleteUser(currentUser.getId(), code);

        return ResponseEntity.ok(Result.of(response));
    }

    // 탈퇴할때 작성할 회원의 메일 조회 - depreciated
    @GetMapping("/mail")
    public ResponseEntity<Result> getMail(@AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        UserMailResponse response = userService.getMail(currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }
}
