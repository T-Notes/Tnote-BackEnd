package com.example.tnote.boundedContext.plan.service;

import com.example.tnote.boundedContext.plan.dto.PlanSaveRequest;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.repository.PlanRepository;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository,
                       ScheduleRepository scheduleRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public PlanResponse save(final Long userId, final Long scheduleId, final PlanSaveRequest registerRequest) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        Plan plan = registerRequest.toEntity(user, schedule);
        return PlanResponse.from(planRepository.save(plan));
    }

}
