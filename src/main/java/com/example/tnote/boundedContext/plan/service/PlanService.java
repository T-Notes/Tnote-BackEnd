package com.example.tnote.boundedContext.plan.service;

import com.example.tnote.boundedContext.plan.repository.PlanRepository;
import org.springframework.transaction.annotation.Transactional;import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }
}
