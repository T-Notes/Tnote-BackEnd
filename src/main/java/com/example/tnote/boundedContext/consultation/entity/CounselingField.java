package com.example.tnote.boundedContext.consultation.entity;

public enum CounselingField {
    FRIENDSHIP("교우관계"),
    GRADE("성적"),
    HOME("가정"),
    HEALTH("건강"),
    ETC("기타");

    private final String fieldName;

    CounselingField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
