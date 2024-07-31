package com.example.tnote.boundedContext.proceeding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingDeleteResponseDto {
    private Long id;

    public static ProceedingDeleteResponseDto of(Long proceedingId) {
        return ProceedingDeleteResponseDto.builder()
                .id(proceedingId)
                .build();
    }
}
