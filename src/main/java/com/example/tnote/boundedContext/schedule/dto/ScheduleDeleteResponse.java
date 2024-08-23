package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleDeleteResponse {
    private Long id;

    public static ScheduleDeleteResponse from(Schedule schedule) {
        return ScheduleDeleteResponse.builder()
                .id(schedule.getId())
                .build();
    }
}
