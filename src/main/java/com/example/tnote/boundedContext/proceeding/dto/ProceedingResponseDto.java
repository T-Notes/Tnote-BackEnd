package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingResponseDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;

    public static ProceedingResponseDto of(Proceeding proceeding) {
        return ProceedingResponseDto.builder()
                .id(proceeding.getId())
                .title(proceeding.getTitle())
                .startDate(proceeding.getStartDate())
                .endDate(proceeding.getEndDate())
                .location(proceeding.getLocation())
                .workContents(proceeding.getWorkContents())
                .build();
    }
}
