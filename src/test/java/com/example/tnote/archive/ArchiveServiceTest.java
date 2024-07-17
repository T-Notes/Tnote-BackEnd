package com.example.tnote.archive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.archive.service.ArchiveService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.schedule.dto.SemesterNameResponseDto;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ArchiveServiceTest {

    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    ArchiveService homeService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    PrincipalDetails principalDetails;
    Consultation consultation;
    ClassLog classLog;
    Proceeding proceeding;
    Observation observation;
    Schedule schedule1;
    Schedule schedule2;

    @BeforeEach
    void before() {
        user1 = testSyUtils.createUser("user1@test.com", "user1", "신갈고등학교", "체육", 4, true);

        principalDetails = principalDetailService.loadUserByUsername(user1.getEmail());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        consultation = testSyUtils.createConsultation("a", date, date, CounselingField.HEALTH, CounselingType.STUDENT,
                "a", "a");
        observation = testSyUtils.createObservation("a", date, date, "a", "a");
        classLog = testSyUtils.createClassLog("a", date, date, "a", "a", "a", "a");
        proceeding = testSyUtils.createProceeding("a", date, date, "a", "a");

        schedule1 = testSyUtils.createSchedule("2024년 3학년 1학기", "a", user1, LocalDate.now(), LocalDate.now());
        schedule2 = testSyUtils.createSchedule("2024년 3학년 2학기", "a", user1, LocalDate.now(), LocalDate.now());
    }

    @Test
    @DisplayName("학생 이름 검색 : 상담기록 - 성공")
    void findAllOfConsultation() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ConsultationResponseDto> response = homeService.findAllOfConsultation(consultation.getTitle(),
                user1.getId(), schedule1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getTitle()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getConsultationContents()).isEqualTo("a");
        assertThat(response.get(0).getCounselingField()).isEqualTo(CounselingField.HEALTH);
        assertThat(response.get(0).getConsultationResult()).isEqualTo("a");
        assertThat(response.get(0).getCounselingType()).isEqualTo(CounselingType.STUDENT);
    }

    @Test
    @DisplayName("다른 유저 상담 일지 조회 - 실패")
    void otherUserFindAllOfConsultation() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(
                () -> homeService.findAllOfConsultation(consultation.getTitle(), 222L, schedule1.getId()))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("학생 이름 검색 : 관찰기록 - 성공")
    void findAllOfObservation() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ObservationResponseDto> response = homeService.findAllOfObservation(observation.getTitle(),
                user1.getId(), schedule1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getTitle()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getObservationContents()).isEqualTo("a");
        assertThat(response.get(0).getGuidance()).isEqualTo("a");

    }

    @Test
    @DisplayName("다른 유저 학급 관찰 조회 - 실패")
    void otherUserFindAllOfObservation() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(
                () -> homeService.findAllOfObservation(observation.getTitle(), 222L, schedule1.getId()))
                .isInstanceOf(CustomExceptions.class);
    }


    @Test
    @DisplayName("제 검색 : 학급일지 - 성공")
    void findAllOfClassLog() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ClassLogResponseDto> response = homeService.findAllOfClassLog(classLog.getTitle(),
                user1.getId(), schedule1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getTitle()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getPlan()).isEqualTo("a");
        assertThat(response.get(0).getClassContents()).isEqualTo("a");
        assertThat(response.get(0).getSubmission()).isEqualTo("a");
        assertThat(response.get(0).getMagnitude()).isEqualTo("a");

    }


    @Test
    @DisplayName("다른  유저 학급 일지 조회 - 실패")
    void otherUserFindAllOfClassLog() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> homeService.findAllOfClassLog(classLog.getTitle(), 222L, schedule1.getId()))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("제목 검색 : 업무일지 - 성공")
    void findAllOfProceeding() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ProceedingResponseDto> response = homeService.findAllOfProceeding(proceeding.getTitle(),
                user1.getId(), schedule1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getTitle()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getLocation()).isEqualTo("a");
        assertThat(response.get(0).getWorkContents()).isEqualTo("a");

    }

    @Test
    @DisplayName("다른 유저 업무 일지 조회 - 실패")
    void otherUserFindAllOfProceeding() {

        // given
        testSyUtils.login(principalDetails);

        // when

        // then
        assertThatThrownBy(() -> homeService.findAllOfProceeding(proceeding.getTitle(), 222L, schedule1.getId()))
                .isInstanceOf(CustomExceptions.class);
    }

    @Test
    @DisplayName("아카이브명 ( 학기명 ) 검색 - 성공")
    void searchSemester() {
        // given
        testSyUtils.login(principalDetails);

        // when
        List<SemesterNameResponseDto> response = scheduleService.searchSemester("2024년 3학년 1학기",
                user1.getId());

        // then
        assertThat(response.get(0).getSemesterName()).isEqualTo(schedule1.getSemesterName());
    }

    @Test
    @DisplayName("로그인 안한 유저 아카이브명 ( 학기명 ) 검색 - 실패")
    void notLoginSearchSemester() {
        // given

        // when

        // then
        assertThatThrownBy(() -> scheduleService.searchSemester("2024년 3학년 1학기", null))
                .isInstanceOf(CustomExceptions.class);
    }

}
