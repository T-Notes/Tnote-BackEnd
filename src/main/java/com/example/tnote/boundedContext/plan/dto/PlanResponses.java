package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Plan;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
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

    public static PlanResponses of(final List<PlanResponse> planResponses, final List<Plan> plans, final Slice<Plan> slice) {
        return PlanResponses.builder()
                .plans(planResponses)
                .numberOfClassLog(plans.size())
                .page(slice.getPageable().getPageNumber())
                .isLast(slice.isLast())
                .build();
    }
}
