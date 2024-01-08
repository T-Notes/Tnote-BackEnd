package com.example.tnote.boundedContext.proceeding.dto;

import lombok.Getter;

@Getter
public class ProceedingUpdateRequestDto {
    private String location;
    private String workContents;

    public boolean isNoLocation() {
        return location != null;
    }

    public boolean isNonWorkContents() {
        return workContents != null;
    }
}
