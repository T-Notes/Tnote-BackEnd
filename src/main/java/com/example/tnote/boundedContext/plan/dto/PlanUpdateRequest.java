package com.example.tnote.boundedContext.plan.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PlanUpdateRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String contents;
    private String participants;

    public PlanUpdateRequest() {
    }

    public PlanUpdateRequest(String title, LocalDateTime startDate, LocalDateTime endDate, String location,
                             String contents, String participants) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.contents = contents;
        this.participants = participants;
    }

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasContents() {
        return contents != null;
    }

    public boolean hasParticipants() {
        return participants != null;
    }
}
