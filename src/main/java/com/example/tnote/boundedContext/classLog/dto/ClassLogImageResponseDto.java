package com.example.tnote.boundedContext.classLog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogImageResponseDto {
    private String url;
    private String originalFileName;
}
