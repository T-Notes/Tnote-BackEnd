package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;

@Getter
public class ProceedingSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    private boolean isAllDay;
    private String color;

    public ProceedingSaveRequest() {
    }

    public ProceedingSaveRequest(String title, LocalDateTime startDate, LocalDateTime endDate, String location,
                                 String workContents, boolean isAllDay, String color) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.workContents = workContents;
        this.isAllDay = isAllDay;
        this.color = color;
    }

    public Proceeding toEntity(final User user, final Schedule schedule) {
        return new Proceeding(this.title, this.startDate, this.endDate, this.location, this.workContents, this.color,
                user, schedule, new ArrayList<>());
    }
}
