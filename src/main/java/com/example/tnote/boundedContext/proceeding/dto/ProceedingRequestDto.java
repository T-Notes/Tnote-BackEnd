package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private boolean isAllDay;

    public Proceeding toEntity(User user) {
        return Proceeding.builder()
                .user(user)
                .title(this.title)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.endDate, this.isAllDay))
                .location(this.location)
                .workContents(this.workContents)
                .proceedingImage(new ArrayList<>())
                .build();
    }
}
