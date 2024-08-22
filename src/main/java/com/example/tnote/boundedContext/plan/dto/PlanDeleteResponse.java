package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Plan;
import lombok.Getter;

@Getter
public class PlanDeleteResponse {
    private Long id;

    public PlanDeleteResponse() {
    }

    public PlanDeleteResponse(final Long id) {
        this.id = id;
    }

}
