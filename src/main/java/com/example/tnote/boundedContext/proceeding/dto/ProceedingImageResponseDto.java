package com.example.tnote.boundedContext.proceeding.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingImageResponseDto {
    private String url;
    private String originalFileName;
}
