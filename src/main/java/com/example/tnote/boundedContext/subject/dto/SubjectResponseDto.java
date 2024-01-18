package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubjectResponseDto {

    private Long id;
    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;
    private String color;
    private LocalDate date;
    private String semesterName;  // 연관관계를 위함.

    public static SubjectResponseDto of(Subjects subject) {

        return SubjectResponseDto.builder()
                .id(subject.getId())
                .subjectName(subject.getSubjectName())
                .classTime(subject.getClassTime())
                .classDay(subject.getClassDay())
                .classLocation(subject.getClassLocation())
                .memo(subject.getMemo())
                .color(subject.getColor())
                .date(subject.getDate())
                .semesterName(subject.getSchedule().getSemesterName())
                .build();
    }

    public static List<SubjectResponseDto> of(List<Subjects> subject) {
        return subject.stream()
                .map(SubjectResponseDto::of)
                .toList();
    }
}
