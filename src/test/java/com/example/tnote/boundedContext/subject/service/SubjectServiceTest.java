package com.example.tnote.boundedContext.subject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.entity.Subjects;
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
class SubjectServiceTest {
    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    PrincipalDetails principalDetails;
    Schedule schedule1;
    Subjects subjects;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);

        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());

        schedule1 = testSyUtils.createSchedule("test1", "9교시", user1, LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-06-01"));
        subjects = testSyUtils.createSubjects("3학년 1학기", "4교시", ClassDay.WEDNESDAY, "3반 교실", "memo", "green",
                LocalDate.parse("2024-03-01"), schedule1);
    }

    @Test
    @DisplayName("과목 작성 성공")
    void addSubjects() {

        // given
        testSyUtils.login(principalDetails);

        SubjectRequestDto dto = SubjectRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .scheduleId(schedule1.getId())
                .build();

        // when
        Subjects sub = dto.toEntity(schedule1);

        // then
        assertThat(sub.getSubjectName()).isEqualTo("물리");
        assertThat(sub.getDate()).isEqualTo(LocalDate.parse("2024-01-27"));
        assertThat(sub.getColor()).isEqualTo("green");
        assertThat(sub.getMemo()).isEqualTo("test1");
        assertThat(sub.getClassTime()).isEqualTo("9교시");
        assertThat(sub.getClassDay()).isEqualTo(ClassDay.WEDNESDAY);
        assertThat(sub.getClassLocation()).isEqualTo("3반교실");
        assertThat(sub.getSchedule().getId()).isEqualTo(schedule1.getId());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저가 과목 작성 실패")
    void notLoginAddSubjects() {

        // given

        SubjectRequestDto dto = SubjectRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .scheduleId(schedule1.getId())
                .build();

        // when

        // then
        assertThatThrownBy(() -> subjectService.addSubjects(dto, null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("과목 정보 수정 - 성공")
    void updateSubjects() {

        // given
        testSyUtils.login(principalDetails);

        SubjectsUpdateRequestDto dto = SubjectsUpdateRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .build();

        // when
        SubjectResponseDto response = subjectService.updateSubjects(dto, subjects.getId(), user1.getId());

        // then
        assertThat(response.getSubjectName()).isEqualTo("물리");
        assertThat(response.getDate()).isEqualTo(LocalDate.parse("2024-01-27"));
        assertThat(response.getColor()).isEqualTo("green");
        assertThat(response.getMemo()).isEqualTo("test1");
        assertThat(response.getClassTime()).isEqualTo("9교시");
        assertThat(response.getClassDay()).isEqualTo(ClassDay.WEDNESDAY);
        assertThat(response.getClassLocation()).isEqualTo("3반교실");
        assertThat(response.getSemesterName()).isEqualTo(schedule1.getSemesterName());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저 과목 정보 수정 - 실패")
    void notLoginUpdateSubjects() {

        // given
        SubjectsUpdateRequestDto dto = SubjectsUpdateRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .build();

        // when

        // then
        assertThatThrownBy(() -> subjectService.updateSubjects(dto, subjects.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("존재 하지 않은 과목 정보 수정 - 실패")
    void notExistUpdateSubjects() {

        // given
        testSyUtils.login(principalDetails);

        SubjectsUpdateRequestDto dto = SubjectsUpdateRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .build();

        // when

        // then
        assertThatThrownBy(() -> subjectService.updateSubjects(dto, null, user1.getId()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("다른 유저 과목 정보 수정 - 실패")
    void otherUserUpdateSubjects() {

        // given
        testSyUtils.login(principalDetails);

        SubjectsUpdateRequestDto dto = SubjectsUpdateRequestDto.builder()
                .memo("test1")
                .classLocation("3반교실")
                .classTime("9교시")
                .classDay(ClassDay.WEDNESDAY)
                .subjectName("물리")
                .color("green")
                .date(LocalDate.parse("2024-01-27"))
                .build();

        // when

        // then
        assertThatThrownBy(() -> subjectService.updateSubjects(dto, subjects.getId(), 222L))
                .isInstanceOf(UserException.class);
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