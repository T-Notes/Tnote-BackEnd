package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectDetailResponseDto {
    private Long id;
    private String subjectName;
    private String classTime;
    private String classDay;
    private String classLocation;
    private String memo;

    public static SubjectDetailResponseDto of(Subjects subject) {

        return SubjectDetailResponseDto.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .classTime(subject.getClassTime())
                .classDay(subject.getClassDay().getDay())
                .classLocation(subject.getClassLocation())
                .memo(subject.getMemo())
                .build();
    }
}
