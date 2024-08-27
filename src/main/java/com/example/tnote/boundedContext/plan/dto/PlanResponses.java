package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Plan;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public class PlanResponses {
    private List<PlanResponse> plans;
    private int numberOfClassLog;
    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public PlanResponses() {
    }

    public PlanResponses(List<PlanResponse> plans, int numberOfClassLog, long page, Boolean isLast) {
        this.plans = plans;
        this.numberOfClassLog = numberOfClassLog;
        this.page = page;
        this.isLast = isLast;
    }

    public static PlanResponses of(final List<PlanResponse> responses, final List<Plan> plans,
                                   final Slice<Plan> slice) {
        return new PlanResponses(responses, plans.size(),
                slice.getPageable().getPageNumber(), slice.isLast());
    }
}
