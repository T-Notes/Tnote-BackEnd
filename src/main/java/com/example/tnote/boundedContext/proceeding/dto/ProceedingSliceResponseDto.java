package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class ProceedingSliceResponseDto {
    List<ProceedingResponseDto> proceedings;
    private int numberOfProceeding;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public static ProceedingSliceResponseDto from(List<ProceedingResponseDto> proceedingDtos,
                                                  List<Proceeding> proceedingList,
                                                  Slice<Proceeding> allProceedingSlice) {
        return ProceedingSliceResponseDto.builder().proceedings(proceedingDtos)
                .numberOfProceeding(proceedingList.size())
                .page(allProceedingSlice.getPageable().getPageNumber())
                .isLast(allProceedingSlice.isLast()).build();
    }
}
