package com.example.tnote.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequestDto;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    Schedule schedule2;
    Subjects subjects1;
    Subjects subjects2;
    Subjects subjects3;
    Subjects subjects4;
    Subjects subjects5;
    PrincipalDetails principalDetails;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);
        schedule1 = testSyUtils.createSchedule("test1", "9교시", user1, LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-06-01"));
        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());

        // 스케쥴 내에서 과목 조회를 위한 더미 데이터 추가
        List<Subjects> subjectsList = new ArrayList<>();
        subjects1 = testSyUtils.createSubjects("물리", "1교시", ClassDay.MONDAY, "3반교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);
        subjects2 = testSyUtils.createSubjects("물리", "1교시", ClassDay.TUESDAY, "3반교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);
        subjects3 = testSyUtils.createSubjects("물리", "1교시", ClassDay.WEDNESDAY, "3반교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);
        subjects4 = testSyUtils.createSubjects("물리", "1교시", ClassDay.THURSDAY, "3반교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);
        subjects5 = testSyUtils.createSubjects("물리", "1교시", ClassDay.FRIDAY, "3반교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);

        subjectsList.add(subjects1);
        subjectsList.add(subjects2);
        subjectsList.add(subjects3);
        subjectsList.add(subjects4);
        subjectsList.add(subjects5);

        schedule2 = testSyUtils.createSchedule("test1", "9교시", user1, LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-06-01"), subjectsList);
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
    @DisplayName("학기 남은 날짜 계산")
    void countLeftDays() {

        // given
        testSyUtils.login(principalDetails);

        // when
        long diffDays = scheduleService.countLeftDays(schedule1.getStartDate(), schedule1.getId());

        // then
        assertThat(diffDays).isEqualTo(92);
    }

    @Test
    @DisplayName("월~금 시간표에 넣을 데이터 조회 성공")
    void getAll() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ScheduleResponseDto> all = scheduleService.getAllSubjectsInfoBySchedule(schedule1.getId(), user1.getId());

        // then
        assertThat(all.get(0).getSemesterName()).isEqualTo(schedule1.getSemesterName());
        assertThat(all.get(0).getLastClass()).isEqualTo(schedule1.getLastClass());
        assertThat(all.get(0).getEmail()).isEqualTo(schedule1.getUser().getEmail());
        assertThat(all.get(0).getStartDate()).isEqualTo(schedule1.getStartDate());
        assertThat(all.get(0).getEndDate()).isEqualTo(schedule1.getEndDate());
    }

    @Test
    @DisplayName("존재하지 않는 학기 월~금 시간표에 넣을 데이터 조회 실패")
    void notExistGetAll() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.getAllSubjectsInfoBySchedule(222L, user1.getId()))
                .isInstanceOf(ScheduleException.class);
    }

    @Test
    @DisplayName("다른 유저의 학기 월~금 시간표에 넣을 데이터 조회 실패")
    void otherUserGetAll() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.getAllSubjectsInfoBySchedule(schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 학기 월~금 시간표에 넣을 데이터 조회 실패")
    void notLoginGetAll() {

        // given

        // when

        // then
        assertThatThrownBy(() -> scheduleService.getAllSubjectsInfoBySchedule(schedule1.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("학기 조회 성공")
    void findSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ScheduleResponseDto> schedule = scheduleService.findSchedule(schedule1.getId(), user1.getId());

        // then
        assertThat(schedule.get(0).getSemesterName()).isEqualTo(schedule1.getSemesterName());
        assertThat(schedule.get(0).getLastClass()).isEqualTo(schedule1.getLastClass());
        assertThat(schedule.get(0).getEmail()).isEqualTo(schedule1.getUser().getEmail());
        assertThat(schedule.get(0).getStartDate()).isEqualTo(schedule1.getStartDate());
        assertThat(schedule.get(0).getEndDate()).isEqualTo(schedule1.getEndDate());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저의 학기 조회 실패")
    void notLoginFindSchedule() {

        // given

        // when

        // then
        assertThatThrownBy(() -> scheduleService.findSchedule(schedule1.getId(), null))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("다른 유저의 학기 조회 실패")
    void otherUserFindSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.findSchedule(schedule1.getId(), 222L))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("존재하지 않는 학기 조회 실패")
    void notExistFindSchedule() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> scheduleService.findSchedule(null, user1.getId()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("학기 남은 수업 조회 성공")
    void countLeftClasses() {

        // given
        testSyUtils.login(principalDetails);

        LocalDate startDate = LocalDate.parse("2024-03-01");
        LocalDate endDate = LocalDate.parse("2024-06-01");

        // when
        long countClasses = scheduleService.countLeftClasses(startDate, endDate, schedule2.getId());

        // then
        assertThat(countClasses).isEqualTo(14);
    }
}