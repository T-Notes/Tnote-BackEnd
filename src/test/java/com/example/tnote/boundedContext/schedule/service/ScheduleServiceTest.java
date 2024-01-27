package com.example.tnote.boundedContext.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequestDto;
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
    @DisplayName("학기 세부 정보 수정 성공")
    void updateSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        ScheduleUpdateRequestDto dto = ScheduleUpdateRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        ScheduleResponseDto response = scheduleService.updateSchedule(dto, schedule1.getId(), user1.getId());

        // then
        assertThat(response.getSemesterName()).isEqualTo(dto.getSemesterName());
        assertThat(response.getLastClass()).isEqualTo(dto.getLastClass());
        assertThat(response.getEmail()).isEqualTo(dto.getEmail());
        assertThat(response.getStartDate()).isEqualTo(dto.getStartDate());
        assertThat(response.getEndDate()).isEqualTo(dto.getEndDate());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 학기 세부 정보 수정 실패")
    void otherUserUpdateSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        ScheduleUpdateRequestDto dto = ScheduleUpdateRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        // then
        assertThatThrownBy(() -> scheduleService.updateSchedule(dto, schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("존재 하지 않은 학기 세부 정보 수정 실패")
    void notExistUpdateSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        ScheduleUpdateRequestDto dto = ScheduleUpdateRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        // then
        assertThatThrownBy(() -> scheduleService.updateSchedule(dto, 222L, user1.getId()))
                .isInstanceOf(ScheduleException.class);
    }

    @Test
    @DisplayName("다른 유저의 학기 세부 정보 수정 실패")
    void notLoginUpdateSchedule() {

        // given

        // when

        ScheduleUpdateRequestDto dto = ScheduleUpdateRequestDto.builder()
                .semesterName("test1")
                .lastClass("9교시")
                .email(user1.getEmail())
                .startDate(LocalDate.parse("2024-03-01"))
                .endDate(LocalDate.parse("2024-06-01"))
                .build();

        // then
        assertThatThrownBy(() -> scheduleService.updateSchedule(dto, schedule1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("학기 정보 삭제 성공")
    void deleteSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        scheduleService.deleteSchedule(schedule1.getId(), user1.getId());
    }

    @Test
    @DisplayName("존재하지 않는 학기 정보 삭제 실패")
    void notExistDeleteSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(222L, user1.getId()))
                .isInstanceOf(ScheduleException.class);
    }

    @Test
    @DisplayName("다른 유저의 학기 정보 삭제 실패")
    void otherUserDeleteSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 학기 정보 삭제 실패")
    void notLoginDeleteSchedule() {

        // given

        // when

        // then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(schedule1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
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