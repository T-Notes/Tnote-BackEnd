package com.example.tnote.boundedContext.subject.dto;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectsDeleteResponseDto {
    private Long id;

    public static SubjectsDeleteResponseDto of(Subjects subject) {
        return SubjectsDeleteResponseDto.builder()
                .id(subject.getId())
                .build();
    }
}