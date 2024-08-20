package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class ProceedingResponses {
    private List<ProceedingResponse> proceedings;

    private int numberOfProceeding;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public ProceedingResponses() {
    }

    public ProceedingResponses(final List<ProceedingResponse> proceedings, final int numberOfProceeding,
                               final long page,
                               final Boolean isLast) {
        this.proceedings = proceedings;
        this.numberOfProceeding = numberOfProceeding;
        this.page = page;
        this.isLast = isLast;
    }

    public static ProceedingResponses of(final List<ProceedingResponse> responses,
                                         final List<Proceeding> proceedingList,
                                         final Slice<Proceeding> allProceedingSlice) {

        return ProceedingResponses.builder().proceedings(responses)
                .numberOfProceeding(proceedingList.size())
                .page(allProceedingSlice.getPageable().getPageNumber())
                .isLast(allProceedingSlice.isLast()).build();
    }
}
