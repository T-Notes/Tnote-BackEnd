package com.example.tnote.boundedContext.plan.repository;

import com.example.tnote.boundedContext.plan.entity.PlanImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanImageRepository extends JpaRepository<PlanImage, Long> {
    @Modifying
    @Query("DELETE FROM PlanImage p WHERE p.plan.id = :planId")
    void deleteByPlanId(@Param("planId") Long planId);
}
