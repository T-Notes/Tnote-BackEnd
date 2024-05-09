package com.example.tnote.boundedContext.home.dto;

import com.example.tnote.boundedContext.home.entity.LastSchedule;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LastScheduleResponseDto {

    private Long id;
    private Long userId;
    private Long scheduleId;

    public static LastScheduleResponseDto of(LastSchedule schedule) {

        return LastScheduleResponseDto.builder()
                .id(schedule.getId())
                .userId(schedule.getUser().getId())
                .scheduleId(schedule.getSchedule().getId())
                .build();
    }
}
