package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProceedingDetailResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;

    public ProceedingDetailResponseDto(Proceeding proceeding) {
        this.id = proceeding.getId();
        this.userId = proceeding.getUser().getId();
        this.title = proceeding.getTitle();
        this.startDate = proceeding.getStartDate();
        this.endDate = proceeding.getEndDate();
        this.location = proceeding.getLocation();
        this.workContents = proceeding.getWorkContents();
    }
}
