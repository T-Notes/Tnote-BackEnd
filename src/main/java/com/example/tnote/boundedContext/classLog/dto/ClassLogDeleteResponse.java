package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogDeleteResponse {
    private final Long id;

    public static ClassLogDeleteResponse from(ClassLog classLog) {
        return ClassLogDeleteResponse.builder()
                .id(classLog.getId())
                .build();
    }
}