package com.example.tnote.boundedContext.plan.repository;

import com.example.tnote.boundedContext.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
