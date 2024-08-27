package com.example.tnote.boundedContext.observation.dto;

import lombok.Getter;

@Getter
public class ObservationDeleteResponse {
    private Long id;

    public static ObservationDeleteResponse from(final Long id){
        return new ObservationDeleteResponse(id);
    }

    public ObservationDeleteResponse() {
    }

    public ObservationDeleteResponse(Long id) {
        this.id = id;
    }
}
