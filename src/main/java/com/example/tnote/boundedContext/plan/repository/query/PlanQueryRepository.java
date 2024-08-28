package com.example.tnote.boundedContext.plan.repository.query;

import static com.example.tnote.boundedContext.plan.entity.QPlan.plan;

import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlanQueryRepository {
    private final JPAQueryFactory query;

    public List<PlanResponse> findAll(Long userId, String keyword, Long scheduleId) {
        List<Plan> planData = query
                .selectFrom(plan)
                .where(
                        plan.schedule.id.eq(scheduleId)
                                .and(plan.title.like("%" + keyword + "%"))
                                .and(plan.user.id.eq(userId))
                )
                .orderBy(plan.id.desc())
                .fetch();

        return planData.stream()
                .map(PlanResponse::from)
                .toList();
    }
}
