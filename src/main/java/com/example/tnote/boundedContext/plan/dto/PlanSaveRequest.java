package com.example.tnote.boundedContext.plan.dto;

import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;

@Getter
public class PlanSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String contents;
    private String participants;
    private String color;
    //todo 참석자 필드 어떻게 요청받을지 정해지면 필드 추가 and 추후에 추가될 기능이라고함

    public Plan toEntity(User user, Schedule schedule) {
        return new Plan(this.title, this.startDate, this.endDate, this.contents, this.location, this.participants,
                this.color, user, schedule, new ArrayList<>());
    }

    public PlanSaveRequest(String title, LocalDateTime startDate, LocalDateTime endDate, String location,
                           String contents, String participants, String color) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.contents = contents;
        this.participants = participants;
        this.color = color;
    }
}
