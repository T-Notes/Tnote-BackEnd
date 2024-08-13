package com.example.tnote.boundedContext.plan.repository;

import com.example.tnote.boundedContext.plan.entity.Plan;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query("select p from Plan p where p.user.id = :userId and p.schedule.id = :scheduleId")
    List<Plan> findALLByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select p from Plan p where p.user.id = :userId and p.schedule.id = :scheduleId ORDER BY p.createdAt DESC")
    Slice<Plan> findALLByUserIdAndScheduleId(Long userId, Long scheduleId, Pageable pageable);
}
