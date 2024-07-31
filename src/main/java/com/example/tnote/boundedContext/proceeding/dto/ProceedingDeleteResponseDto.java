package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingDeleteResponseDto {
    private Long id;

    public static ProceedingDeleteResponseDto of(Proceeding proceeding) {
        return ProceedingDeleteResponseDto.builder()
                .id(proceeding.getId())
                .build();
    }
}
