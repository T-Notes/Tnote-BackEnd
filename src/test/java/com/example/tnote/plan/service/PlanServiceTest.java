package com.example.tnote.plan.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.dto.PlanSaveRequest;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.repository.PlanRepository;
import com.example.tnote.boundedContext.plan.service.PlanService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import com.example.tnote.utils.TestSyUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;


    @Nested
    @DisplayName("일정 생성/삭제")
    class saveAndDelete {

        @DisplayName("일정 생성")
        @Test
        void save() {
            User user = mock(User.class);

            LocalDate startDate = LocalDate.of(2024, 1, 10);
            LocalDate endDate = LocalDate.of(2024, 1, 20);
            Schedule schedule = new Schedule(1L, "1학기", null,
                    startDate, endDate, user);

            LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 11, 0, 0);
            LocalDateTime endDateTime = LocalDateTime.of(2024, 1, 19, 0, 0);
            PlanSaveRequest request = new PlanSaveRequest("Development", startDateTime, endDateTime, "Seoul", "Project Development", "Team");

            when(userRepository.findUserById(1L)).thenReturn(user);
            when(scheduleRepository.findScheduleById(1L)).thenReturn(schedule);
            when(planRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PlanResponse result = planService.save(1L, 1L, request, null);

            assertThat(result).isNotNull();
        }
    }
}