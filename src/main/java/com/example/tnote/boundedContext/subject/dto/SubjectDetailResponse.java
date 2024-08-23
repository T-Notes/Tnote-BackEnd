package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectDetailResponse {
    private Long id;
    private String subjectName;
    private String classTime;
    private String classDay;
    private String classLocation;
    private String memo;
    private String color;

    public static SubjectDetailResponse from(Subjects subject) {

        return SubjectDetailResponse.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .classTime(subject.getClassTime())
                .classDay(subject.getClassDay().getDay())
                .classLocation(subject.getClassLocation())
                .memo(subject.getMemo())
                .color(subject.getColor())
                .build();
    }
}
