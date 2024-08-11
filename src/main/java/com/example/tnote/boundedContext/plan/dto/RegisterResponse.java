package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Participant;
import com.example.tnote.boundedContext.plan.entity.Plan;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String contents;
    private List<Participant> participantList;

    public static RegisterResponse from(final Plan plan) {
        return RegisterResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .location(plan.getLocation())
                .contents(plan.getContents())
                .participantList(plan.getParticipant())
                .build();
    }
}
