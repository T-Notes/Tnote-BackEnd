package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;

    //todo 추후에 S3로 이미지도 추가해야합니다.
    public Proceeding toEntity() {
        return Proceeding.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .location(location)
                .workContents(workContents)
                .build();
    }
}
