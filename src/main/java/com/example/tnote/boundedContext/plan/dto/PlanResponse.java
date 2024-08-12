package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.entity.PlanImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanResponse {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String contents;
    private List<PlanImage> planImageList;

    public static PlanResponse from(final Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .location(plan.getLocation())
                .contents(plan.getContents())
                .planImageList(plan.getPlanImages())
                .build();
    }
}
