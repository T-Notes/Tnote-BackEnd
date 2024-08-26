package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;

@Getter
public class ObservationSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private boolean isAllDay;
    private String color;

    public Observation toEntity(final User user, final Schedule schedule) {
        return new Observation(this.title, this.startDate, this.endDate, this.observationContents, this.guidance,
                this.color, user, schedule, new ArrayList<>());
    }

    public ObservationSaveRequest() {
    }

    public ObservationSaveRequest(String title, LocalDateTime startDate, LocalDateTime endDate,
                                  String observationContents,
                                  String guidance, boolean isAllDay, String color) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.observationContents = observationContents;
        this.guidance = guidance;
        this.isAllDay = isAllDay;
        this.color = color;
    }
}
