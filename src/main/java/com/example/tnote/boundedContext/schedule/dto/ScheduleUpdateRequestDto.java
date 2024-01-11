package com.example.tnote.boundedContext.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleUpdateRequestDto {
    private String semesterName;
    private String lastClass;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public boolean hasSemesterName(){
        return semesterName != null;
    }
    public boolean hasLastClass(){
        return lastClass != null;
    }
    public boolean hasStartDate(){
        return startDate != null;
    }
    public boolean hasEndDate(){
        return endDate != null;
    }
}
