package com.example.tnote.boundedContext.schedule.entity;

public enum ClassDay {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private String day;

    private ClassDay(String day) {
        this.day = day;
    }

    public String getClassDay() {
        return day;
    }
}
