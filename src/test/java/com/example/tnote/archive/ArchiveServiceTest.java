package com.example.tnote.archive;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.tnote.boundedContext.archive.service.ArchiveService;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.schedule.dto.SemesterResponse;
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
    ArchiveService archiveService;

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

        schedule1 = testSyUtils.createSchedule("2024년 3학년 1학기", "a", user1, LocalDate.now(), LocalDate.now());
        schedule2 = testSyUtils.createSchedule("2024년 3학년 2학기", "a", user1, LocalDate.now(), LocalDate.now());

        consultation = testSyUtils.createConsultation("a", date, date, CounselingField.HEALTH, CounselingType.STUDENT,
                "a", "a", "red", user1, schedule1);
        observation = testSyUtils.createObservation("a", date, date, "a", "a", "red", user1, schedule1);
        proceeding = testSyUtils.createProceeding("a", date, date, "a", "a", "red", user1, schedule1);
    }

//    @Test
//    @DisplayName("학생 검색 - 성공")
//    void findAll() {
//        // given
//
//        // when
//        ArchiveResponse response = archiveService.findAll(user1.getId(), schedule1.getId(), "a");
//
//        // then
//        assertThat(response.getObservations().get(0).getObservationContents()).isEqualTo("a");
//        assertThat(response.getObservations().get(0).getGuidance()).isEqualTo("a");
//        assertThat(response.getProceedings().get(0).getColor()).isEqualTo("red");
//        assertThat(response.getProceedings().get(0).getLocation()).isEqualTo("a");
//        assertThat(response.getProceedings().get(0).getWorkContents()).isEqualTo("a");
//    }

    @Test
    @DisplayName("아카이브명 ( 학기명 ) 검색 - 성공")
    void searchSemester() {
        // given
        testSyUtils.login(principalDetails);

        // when
        List<SemesterResponse> response = scheduleService.searchSemester("2024년 3학년 1학기",
                user1.getId());

        // then
        assertThat(response.get(0).getSemesterName()).isEqualTo(schedule1.getSemesterName());
    }

}
