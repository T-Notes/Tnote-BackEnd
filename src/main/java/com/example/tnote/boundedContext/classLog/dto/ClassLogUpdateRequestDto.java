package com.example.tnote.boundedContext.classLog.dto;

import lombok.Getter;

@Getter
public class ClassLogUpdateRequestDto {
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public boolean isNonPlan(){
        return plan != null;
    }
    public boolean isNonClassContents(){
        return classContents != null;
    }
    public boolean isNonSubmission(){
        return submission != null;
    }
    public boolean isNonMagnitude(){
        return magnitude != null;
    }
}
