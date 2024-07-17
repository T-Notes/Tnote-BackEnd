package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleDeleteResponseDto {
    private Long id;

    public static ScheduleDeleteResponseDto of(Schedule schedule) {
        return ScheduleDeleteResponseDto.builder()
                .id(schedule.getId())
                .build();
    }
}
