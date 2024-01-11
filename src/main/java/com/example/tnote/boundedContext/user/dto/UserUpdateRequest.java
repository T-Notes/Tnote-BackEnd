package com.example.tnote.boundedContext.user.dto;

import lombok.Getter;

@Getter
public class UserUpdateRequest {

    private String schoolName;
    private String subject;
    private int career;
    private boolean alarm;

    public boolean hasSchoolName(){
        return schoolName != null;
    }
    public boolean hasSubject(){
        return subject != null;
    }
    public boolean hasCareer(){
        return career != 0;
    }
    public boolean hasAlarm(){
        return alarm != false;
    }
}
