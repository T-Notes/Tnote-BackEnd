package com.example.tnote.boundedContext.classLog.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
}
