package com.example.tnote.entity.constant;

public enum CounselingType {
    PATENTS("학부모"),
    STUDENT("학생");

    private final String type;

    CounselingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
