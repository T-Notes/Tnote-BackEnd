package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SemesterNameResponseDto {
    private Long id;
    private String semesterName;

    public static SemesterNameResponseDto from(Schedule schedules) {
        return SemesterNameResponseDto.builder()
                .id(schedules.getId())
                .semesterName(schedules.getSemesterName())
                .build();
    }

    public static List<SemesterNameResponseDto> from(List<Schedule> schedules) {
        return schedules.stream()
                .map(SemesterNameResponseDto::from)
                .toList();
    }
}
