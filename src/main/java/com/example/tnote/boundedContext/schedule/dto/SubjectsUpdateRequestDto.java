package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import lombok.Getter;

@Getter
public class SubjectsUpdateRequestDto {

    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;

    public boolean hasSubjectName(){
        return subjectName != null;
    }
    public boolean hasClassTime(){
        return classTime != null;
    }
    public boolean hasClassDay(){
        return classDay != null;
    }
    public boolean hasClassLocation(){
        return classLocation != null;
    }
    public boolean hasMemo(){
        return memo != null;
    }
}
