package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationRequestDto {
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private boolean isAllDay;

    public Observation toEntity(User user) {
        return Observation.builder()
                .user(user)
                .studentName(this.studentName)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.endDate, this.isAllDay))
                .observationContents(this.observationContents)
                .guidance(this.guidance)
                .observationImage(new ArrayList<>())
                .build();
    }
}
