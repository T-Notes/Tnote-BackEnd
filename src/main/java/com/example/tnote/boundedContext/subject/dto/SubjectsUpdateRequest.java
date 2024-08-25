package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectsUpdateRequest {

    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;
    private String color;
    private LocalDate date;

    public boolean hasSubjectName() {
        return subjectName != null;
    }

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasClassTime() {
        return classTime != null;
    }

    public boolean hasColor() {
        return color != null;
    }

    public boolean hasClassDay() {
        return classDay != null;
    }

    public boolean hasClassLocation() {
        return classLocation != null;
    }

    public boolean hasMemo() {
        return memo != null;
    }
}
