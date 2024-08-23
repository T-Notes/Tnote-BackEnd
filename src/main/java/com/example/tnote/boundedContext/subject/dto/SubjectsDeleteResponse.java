package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectsDeleteResponse {
    private Long id;

    public static SubjectsDeleteResponse from(Subjects subject) {
        return SubjectsDeleteResponse.builder()
                .id(subject.getId())
                .build();
    }
}