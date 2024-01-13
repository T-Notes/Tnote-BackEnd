package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ProceedingRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    private boolean isAllDay;

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
