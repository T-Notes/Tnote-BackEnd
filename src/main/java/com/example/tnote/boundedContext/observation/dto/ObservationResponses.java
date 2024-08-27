package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.observation.entity.Observation;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class ObservationResponses {
    private List<ObservationResponse> observations;
    private int numberOfObservation;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public static ObservationResponses of(final List<ObservationResponse> observationResponseDto,
                                          final List<Observation> observations,
                                          final Slice<Observation> allAbservationsSlice) {
        return ObservationResponses.builder()
                .observations(observationResponseDto)
                .numberOfObservation(observations.size())
                .page(allAbservationsSlice.getPageable().getPageNumber())
                .isLast(allAbservationsSlice.isLast())
                .build();

    }
}
