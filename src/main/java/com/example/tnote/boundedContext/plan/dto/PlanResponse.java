package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.entity.PlanImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanResponse implements LogEntry {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String contents;
    private String participants;
    private String color;
    private LocalDateTime createAt;
    private List<PlanImage> planImageList;

    public static PlanResponse from(final Plan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .location(plan.getLocation())
                .contents(plan.getContents())
                .participants(plan.getParticipants())
                .color(plan.getColor())
                .createAt(plan.getCreatedAt())
                .planImageList(plan.getPlanImages())
                .build();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createAt;
    }
}
