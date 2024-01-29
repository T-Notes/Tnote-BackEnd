package com.example.tnote.home;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.service.auth.PrincipalDetailService;
import com.example.tnote.utils.TestSyUtils;
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
public class homeServiceTest {

    @Autowired
    TestSyUtils testSyUtils;

    @Autowired
    HomeService homeService;

    @Autowired
    PrincipalDetailService principalDetailService;

    User user1;
    PrincipalDetails principalDetails;
    Consultation consultation;
    ClassLog classLog;
    Proceeding proceeding;
    Observation observation;

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
    }

    @Test
    @DisplayName("학생 이름 검색 : 상담기록 - 성공")
    void findAllOfConsultation() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ConsultationResponseDto> response = homeService.findAllOfConsultation(consultation.getStudentName(),
                user1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getStudentName()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getConsultationContents()).isEqualTo("a");
        assertThat(response.get(0).getCounselingField()).isEqualTo(CounselingField.HEALTH);
        assertThat(response.get(0).getConsultationResult()).isEqualTo("a");
        assertThat(response.get(0).getCounselingType()).isEqualTo(CounselingType.STUDENT);

    }

    @Test
    @DisplayName("학생 이름 검색 : 관찰기록 - 성공")
    void findAllOfObservation() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ObservationResponseDto> response = homeService.findAllOfObservation(observation.getStudentName(),
                user1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getStudentName()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getObservationContents()).isEqualTo("a");
        assertThat(response.get(0).getGuidance()).isEqualTo("a");

    }

    @Test
    @DisplayName("제 검색 : 학급일지 - 성공")
    void findAllOfClassLog() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ClassLogResponseDto> response = homeService.findAllOfClassLog(classLog.getTitle(),
                user1.getId());

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
    @DisplayName("제목 검색 : 업무일지 - 성공")
    void findAllOfProceeding() {

        // given
        testSyUtils.login(principalDetails);

        // when
        List<ProceedingResponseDto> response = homeService.findAllOfProceeding(proceeding.getTitle(),
                user1.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime date = LocalDateTime.parse("2024-03-01 13:47:13.248", formatter);

        // then
        assertThat(response.get(0).getTitle()).isEqualTo("a");
        assertThat(response.get(0).getStartDate()).isEqualTo(date);
        assertThat(response.get(0).getEndDate()).isEqualTo(date);
        assertThat(response.get(0).getLocation()).isEqualTo("a");
        assertThat(response.get(0).getWorkContents()).isEqualTo("a");

    }
}
