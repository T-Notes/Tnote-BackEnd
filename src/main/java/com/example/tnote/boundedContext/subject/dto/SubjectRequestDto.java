package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.subject.entity.Subjects;
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
    private String color;
    private Long scheduleId;  // 연관관계를 위함.

    public Subjects toEntity(Schedule schedule) {
        return Subjects.builder()
                .memo(this.memo)
                .classLocation(this.classLocation)
                .classTime(this.classTime)
                .classDay(this.classDay)
                .subjectName(this.subjectName)
                .color(this.color)
                .schedule(schedule)
                .build();
    }
}
