package com.example.tnote.subject.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.subject.dto.SubjectDetailResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.service.SubjectService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
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
                schedule1);
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
                .scheduleId(schedule1.getId())
                .build();

        // when
        Subjects sub = dto.toEntity(schedule1);

        // then
        assertThat(sub.getSubjectName()).isEqualTo("물리");
        assertThat(sub.getColor()).isEqualTo("green");
        assertThat(sub.getMemo()).isEqualTo("test1");
        assertThat(sub.getClassTime()).isEqualTo("9교시");
        assertThat(sub.getClassDay().getDay()).isEqualTo("수요일");
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
                .scheduleId(schedule1.getId())
                .build();

        // when

        // then
        assertThatThrownBy(() -> subjectService.addSubjects(dto, schedule1.getId(), null))
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
        assertThat(response.getColor()).isEqualTo("green");
        assertThat(response.getMemo()).isEqualTo("test1");
        assertThat(response.getClassTime()).isEqualTo("9교시");
        assertThat(response.getClassDay()).isEqualTo(ClassDay.WEDNESDAY.getDay());
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
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("과목 삭제 - 성공")
    void deleteSubjects() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        subjectService.deleteSubjects(schedule1.getId(), subjects.getId(), user1.getId());
    }

    @Test
    @DisplayName("로그인 하지 않은 유저 과목 삭제 - 실패")
    void notLoginDeleteSubjects() {

        // given

        // when

        // then
        assertThatThrownBy(() -> subjectService.deleteSubjects(schedule1.getId(), subjects.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("다른 유저 과목 삭제 - 실패")
    void otherUserDeleteSubjects() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> subjectService.deleteSubjects(schedule1.getId(), subjects.getId(), 222L))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("존재하지 않는 과목 삭제 - 실패")
    void notExistDeleteSubjects() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> subjectService.deleteSubjects(schedule1.getId(), 222L, user1.getId()))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("과목 조회 - 성공")
    void getMyClass() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<SubjectResponseDto> response = subjectService.getMyClass(schedule1.getId(), ClassDay.WEDNESDAY,
                user1.getId());

        // then
        assertThat(response.get(0).getMemo()).isEqualTo("memo");
        assertThat(response.get(0).getColor()).isEqualTo("green");
        assertThat(response.get(0).getClassTime()).isEqualTo("4교시");
        assertThat(response.get(0).getClassDay()).isEqualTo(ClassDay.WEDNESDAY.getDay());
        assertThat(response.get(0).getClassLocation()).isEqualTo("3반 교실");
        assertThat(response.get(0).getSemesterName()).isEqualTo("test1");
    }

    @Test
    @DisplayName("로그인 하지 않은 유저 과목 조회 - 실패")
    void notLoginGetMyClass() {

        // given

        // when

        // then
        assertThatThrownBy(() -> subjectService.getMyClass(schedule1.getId(), ClassDay.WEDNESDAY, null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("다른 유저 과목 조회 - 실패")
    void otherUserGetMyClass() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> subjectService.getMyClass(schedule1.getId(), ClassDay.WEDNESDAY, 222L))
                .isInstanceOf(CustomException.class);
    }

//    @Test
//    @DisplayName("오늘 수업 과목 조회 - 성공")
//    void getTodayClass() {
//        // given
//        testSyUtils.login(principalDetails);
//        LocalDate date = LocalDate.now();
//
//        // when
//        List<SubjectResponseDto> response = subjectService.getTodayClass(schedule1.getId(), user1.getId(), date);
//
//        // then
//        assertThat(response.get(0).getMemo()).isEqualTo("memo");
//        assertThat(response.get(0).getColor()).isEqualTo("green");
//        assertThat(response.get(0).getClassTime()).isEqualTo("4교시");
//        assertThat(response.get(0).getClassDay()).isEqualTo("수요일");
//        assertThat(response.get(0).getClassLocation()).isEqualTo("3반 교실");
//        assertThat(response.get(0).getSemesterName()).isEqualTo("test1");
//    }

    @Test
    @DisplayName("로그인 하지 않은 유저 오늘 수업 과목 조회 - 실패")
    void notLoginGetTodayClass() {
        // given
        LocalDate date = LocalDate.now();

        // when

        // then
        assertThatThrownBy(() -> subjectService.getTodayClass(schedule1.getId(), null, date))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName("다른 유저 오늘 수업 과목 조회 - 실패")
    void otherUserGetTodayClass() {
        // given
        testSyUtils.login(principalDetails);
        LocalDate date = LocalDate.now();

        // when

        // then
        assertThatThrownBy(() -> subjectService.getTodayClass(schedule1.getId(), 222L, date))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName(" 특정 과목 조회 - 성공")
    void getSubject() {
        // given
        testSyUtils.login(principalDetails);

        // when
        SubjectDetailResponseDto response = subjectService.getSubject(schedule1.getId(), subjects.getId(),
                user1.getId());

        // then
        assertThat(response.getSubjectName()).isEqualTo("3학년 1학기");
        assertThat(response.getMemo()).isEqualTo("memo");
        assertThat(response.getClassTime()).isEqualTo("4교시");
        assertThat(response.getClassDay()).isEqualTo(ClassDay.WEDNESDAY.getDay());
        assertThat(response.getClassLocation()).isEqualTo("3반 교실");
    }

    @Test
    @DisplayName(" 로그인 안한 유저 특정 과목 조회 - 실패")
    void notLoginGetSubject() {
        // given

        // when

        // then
        assertThatThrownBy(() -> subjectService.getSubject(schedule1.getId(), subjects.getId(), null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName(" 없는 과목 조회 - 실패")
    void notExistGetSubject() {
        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> subjectService.getSubject(schedule1.getId(), null, user1.getId()))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @DisplayName(" 다른 유저 특정 과목 조회 - 실패")
    void otherUserGetSubject() {
        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> subjectService.getSubject(schedule1.getId(), subjects.getId(), 222L))
                .isInstanceOf(CustomException.class);
    }
}