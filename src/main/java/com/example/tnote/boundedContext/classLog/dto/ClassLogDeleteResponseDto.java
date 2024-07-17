package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogDeleteResponseDto {
    private final Long id;

    public static ClassLogDeleteResponseDto of(ClassLog classLog) {
        return ClassLogDeleteResponseDto.builder()
                .id(classLog.getId())
                .build();
    }
}