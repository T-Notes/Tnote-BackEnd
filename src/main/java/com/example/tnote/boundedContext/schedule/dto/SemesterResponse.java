package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SemesterResponse {
    private Long id;
    private String semesterName;

    public static SemesterResponse from(Schedule schedules) {
        return SemesterResponse.builder()
                .id(schedules.getId())
                .semesterName(schedules.getSemesterName())
                .build();
    }

    public static List<SemesterResponse> from(List<Schedule> schedules) {
        return schedules.stream()
                .map(SemesterResponse::from)
                .toList();
    }
}
