package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingDeleteResponse {
    private Long id;

    public static ProceedingDeleteResponse from(final Proceeding proceeding) {
        return ProceedingDeleteResponse.builder()
                .id(proceeding.getId())
                .build();
    }
}
