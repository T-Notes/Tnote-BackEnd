package com.example.tnote.boundedContext.classLog.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ClassLogUpdateRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public ClassLogUpdateRequest() {
    }

    public ClassLogUpdateRequest(String title, LocalDateTime startDate, LocalDateTime endDate, String plan,
                                 String classContents, String submission, String magnitude) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.plan = plan;
        this.classContents = classContents;
        this.submission = submission;
        this.magnitude = magnitude;
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
