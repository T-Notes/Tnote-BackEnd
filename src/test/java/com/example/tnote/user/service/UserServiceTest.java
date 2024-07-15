package com.example.tnote.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.boundedContext.user.dto.UserAlarmUpdate;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserRequest;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.UserService;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    UserService userService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    User user2;
    PrincipalDetails principalDetails;
    PrincipalDetails principalDetails2;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);
        user2 = testSyUtils.createUser("user2@test.com", "user2");
        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());
        principalDetails2 = principalDetailService.loadUserByUsername(user2.getEmail());
    }

    @Test
    @DisplayName("회원 가입 성공")
    void signUp() {

        // given
        UserRequest request = UserRequest.builder()
                .email("test1@test.com")
                .name("test1")
                .build();

        // when
        UserResponse response =
                userService.signUp(request.getEmail(), request.getName());

        // then
        assertThat(response.getEmail()).isEqualTo("test1@test.com");
        assertThat(response.getName()).isEqualTo("test1");

    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void updateExtraInfo() {

        // given
        testSyUtils.login(principalDetails2);

        // when
        UserUpdateRequest userInfo = UserUpdateRequest.builder()
                .schoolName("변경된 대학교")
                .subject("변경된 과목")
                .career(11)
                .build();

        UserResponse userResponse = userService.updateExtraInfo(user2.getId(), userInfo);

        // then
        assertThat(userResponse.getSubject()).isEqualTo(user2.getSubject());
        assertThat(userResponse.getCareer()).isEqualTo(user2.getCareer());
        assertThat(userResponse.getSchool()).isEqualTo(user2.getSchool());
    }

    @Test
    @DisplayName("유저 알람 정보 수정 성공")
    void update_alarm_info() {

        // given
        testSyUtils.login(principalDetails2);

        // when
        UserAlarmUpdate userInfo = UserAlarmUpdate.builder()
                .alarm(true)
                .build();

        UserResponse userResponse = userService.updateAlarmInfo(user2.getId(), userInfo);

        // then
        assertThat(userResponse.isAlarm()).isEqualTo(user2.isAlarm());
    }

    @Test
    @DisplayName("본인의 정보가 아닌 다른 회원 정보 수정시 실패")
    void otherUserUpdateExtraInfo() {

        // given
        testSyUtils.login(principalDetails);

        // when
        UserUpdateRequest userInfo = UserUpdateRequest.builder()
                .schoolName("변경된 대학교")
                .subject("변경된 과목")
                .career(11)
                .build();

        // then
        assertThatThrownBy(() -> userService.updateExtraInfo(222L, userInfo))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("로그인 하지 않은 회원 정보 수정시 실패")
    void notLoginUpdateExtraInfo() {

        // given

        // when
        UserUpdateRequest userInfo = UserUpdateRequest.builder()
                .schoolName("변경된 대학교")
                .subject("변경된 과목")
                .career(11)
                .build();

        // then
        assertThatThrownBy(() -> userService.updateExtraInfo(null, userInfo))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("유저 정보 조회 성공")
    void getUserInfo() {

        // given
        testSyUtils.login(principalDetails);

        // when
        UserResponse userInfo = userService.getUserInfo(user1.getId());

        // then
        assertThat(userInfo.getName()).isEqualTo(user1.getUsername());
        assertThat(userInfo.getEmail()).isEqualTo(user1.getEmail());
        assertThat(userInfo.getSubject()).isEqualTo(user1.getSubject());
        assertThat(userInfo.getCareer()).isEqualTo(user1.getCareer());
        assertThat(userInfo.getSchool()).isEqualTo(user1.getSchool());
        assertThat(userInfo.isAlarm()).isEqualTo(user1.isAlarm());
    }

    @Test
    @DisplayName("다른 유저 정보 조회 실패")
    void otherUserGetInfo() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> userService.getUserInfo(222L))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("존재하지 않는 유저 정보 조회 실패")
    void noneUserGetInfo() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> userService.getUserInfo(222L))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("유저 정보 로그 아웃 성공")
    void logout() {

        // given
        testSyUtils.login(principalDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        userService.logout(request, response, user1.getId());

        UserResponse updatedUser = userService.getUserInfo(user1.getId());

        // then
        assertThat(updatedUser.getId()).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("다른 유저 정보 로그 아웃 성공")
    void otherUserLogout() {

        // given
        testSyUtils.login(principalDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when

        // then
        assertThatThrownBy(() -> userService.logout(request, response, 222L))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("유저 메일 정보 조회 성공")
    void getMail() {

        // given
        testSyUtils.login(principalDetails);

        // when
        UserMailResponse mail = userService.getMail(user1.getId());

        // then
        assertThat(mail.getEmail()).isEqualTo(user1.getEmail());
        assertThat(mail.getId()).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("다른 유저 메일 정보 조회 실패")
    void otherUserGetMail() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> userService.getMail(222L))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("로그인 하지 않고 유저 메일 정보 조회 실패")
    void notLoginGetMail() {

        // given

        // when

        // then
        assertThatThrownBy(() -> userService.getMail(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }
}
