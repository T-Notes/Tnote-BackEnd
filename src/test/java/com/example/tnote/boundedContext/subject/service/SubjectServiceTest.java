package com.example.tnote.boundedContext.subject.service;

import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SubjectServiceTest {
    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    PrincipalDetails principalDetails;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);

        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());
    }

    @Test
    @DisplayName("과 작성 성공")
    void addSubjects() {

        // given
        testSyUtils.login(principalDetails);

//        SubjectRequestDto dto = SubjectRequestDto.builder()
//                .memo()
//                .classLocation()
//                .classTime()
//                .classDay()
//                .subjectName()
//                .color()
//                .date(LocalDate.parse("2024-01-27"))
//                .scheduleId()
//                .build();

        // when

        // then
    }

    @Test
    void updateSubjects() {
    }

    @Test
    void deleteSubjects() {
    }

    @Test
    void getMyClass() {
    }

    @Test
    void getTodayClass() {
    }
}