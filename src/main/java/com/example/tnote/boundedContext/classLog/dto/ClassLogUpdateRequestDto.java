package com.example.tnote.boundedContext.classLog.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ClassLogUpdateRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }
    public boolean hasPlan() {
        return plan != null;
    }

    public boolean hasClassContents() {
        return classContents != null;
    }

    public boolean hasSubmission() {
        return submission != null;
    }

    public boolean hasMagnitude() {
        return magnitude != null;
    }

}
