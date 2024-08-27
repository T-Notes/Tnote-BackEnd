package com.example.tnote.boundedContext.consultation.entity;

import lombok.Getter;

@Getter
public enum CounselingType {
    PATENTS("학부모"),
    STUDENT("학생");

    private final String type;

    CounselingType(String type) {
        this.type = type;
    }

}
