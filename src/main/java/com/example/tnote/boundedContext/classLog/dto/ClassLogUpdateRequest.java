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

    public ClassLogUpdateRequest(final String title, final LocalDateTime startDate, final LocalDateTime endDate,
                                 final String plan,
                                 final String classContents, final String submission, final String magnitude) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.plan = plan;
        this.classContents = classContents;
        this.submission = submission;
        this.magnitude = magnitude;
    }
}
