package com.example.tnote.boundedContext.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ScheduleServiceTest {
    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    Schedule schedule1;
    PrincipalDetails principalDetails;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);
        schedule1 = testSyUtils.createSchedule("test1", "9교시", user1, LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-06-01"));
        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());
    }

    @Test
    @DisplayName("학기 작성 성공")
    void addSchedule() {

        // given
        testSyUtils.login(principalDetails);

        ScheduleRequestDto dto = ScheduleRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        // when
        Schedule schedule = dto.toEntity(user1);

        // then
        assertThat(schedule.getSemesterName()).isEqualTo("test1");
        assertThat(schedule.getLastClass()).isEqualTo("9교시");
        assertThat(schedule.getUser()).isEqualTo(user1);
        assertThat(schedule.getStartDate()).isEqualTo("2024-03-01");
        assertThat(schedule.getEndDate()).isEqualTo(LocalDate.parse("2024-06-01"));
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 학기 작성 실패")
    void notLoginAddSchedule() {

        // given

        ScheduleRequestDto dto = ScheduleRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        // when

        // then
        assertThatThrownBy(() -> scheduleService.addSchedule(dto, null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateSchedule() {
    }

    @Test
    void deleteSchedule() {
    }

    @Test
    void countLeftDays() {
    }

    @Test
    void getAll() {
    }

    @Test
    void findSchedule() {
    }

    @Test
    void findScheduleList() {
    }

    @Test
    void countLeftClasses() {
    }
}