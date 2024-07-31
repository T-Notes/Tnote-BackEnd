package com.example.tnote.boundedContext.observation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationDeleteResponseDto {
    private Long id;

    public static ObservationDeleteResponseDto of(Long id){
        return ObservationDeleteResponseDto.builder()
                .id(id)
                .build();
    }
}
