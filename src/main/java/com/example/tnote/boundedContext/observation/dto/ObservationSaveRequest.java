package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private boolean isAllDay;
    private String color;

    public Observation toEntity(User user, Schedule schedule) {
        return Observation.builder()
                .user(user)
                .title(this.title)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.endDate, this.isAllDay))
                .observationContents(this.observationContents)
                .guidance(this.guidance)
                .observationImage(new ArrayList<>())
                .schedule(schedule)
                .color(this.color)
                .build();
    }
}
