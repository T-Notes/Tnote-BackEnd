package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubjectResponse {

    private Long id;
    private String subjectName;
    private String classTime;
    private String classDay;
    private String classLocation;
    private String memo;
    private String color;
    private String semesterName;  // 연관관계를 위함.

    public static SubjectResponse from(Subjects subject) {

        return SubjectResponse.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .classTime(subject.getClassTime())
                .classDay(subject.getClassDay().getDay())
                .classLocation(subject.getClassLocation())
                .memo(subject.getMemo())
                .color(subject.getColor())
                .semesterName(subject.getSchedule().getSemesterName())
                .build();
    }

    public static List<SubjectResponse> from(List<Subjects> subject) {
        return subject.stream()
                .map(SubjectResponse::from)
                .toList();
    }

}
