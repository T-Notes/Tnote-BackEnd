package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubjectRequestDto {

    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;
    private Long scheduleId;  // 연관관계를 위함.
}
