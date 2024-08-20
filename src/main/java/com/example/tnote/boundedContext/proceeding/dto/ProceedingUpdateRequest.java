package com.example.tnote.boundedContext.proceeding.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ProceedingUpdateRequest {
    private String location;
    private String workContents;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
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

    public boolean hasWorkContents() {
        return workContents != null;
    }
}
