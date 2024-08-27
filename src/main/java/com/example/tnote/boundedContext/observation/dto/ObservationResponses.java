package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.consultation.dto.ConsultationResponses;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public class ObservationResponses {
    private List<ObservationResponse> observations;
    private int numberOfObservation;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public ObservationResponses() {
    }

    public ObservationResponses(List<ObservationResponse> observations, int numberOfObservation, long page,
                                Boolean isLast) {
        this.observations = observations;
        this.numberOfObservation = numberOfObservation;
        this.page = page;
        this.isLast = isLast;
    }

    public static ObservationResponses of(final List<ObservationResponse> responses,
                                          final List<Observation> observations,
                                          final Slice<Observation> allObservations) {
        return new ObservationResponses(responses, observations.size(),
                allObservations.getPageable().getPageNumber(), allObservations.isLast());

    }
}
