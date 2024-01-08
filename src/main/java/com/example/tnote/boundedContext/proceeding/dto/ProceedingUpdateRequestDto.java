package com.example.tnote.boundedContext.proceeding.dto;

import lombok.Getter;

@Getter
public class ProceedingUpdateRequestDto {
    private String location;
    private String workContents;

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasWorkContents() {
        return workContents != null;
    }
}
