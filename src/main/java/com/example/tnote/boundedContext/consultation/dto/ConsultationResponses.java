package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public class ConsultationResponses {

    private List<ConsultationResponse> consultations;
    private int numberOfConsultation;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public ConsultationResponses() {
    }

    public ConsultationResponses(final List<ConsultationResponse> consultations, final int numberOfConsultation,
                                 final long page, final Boolean isLast) {
        this.consultations = consultations;
        this.numberOfConsultation = numberOfConsultation;
        this.page = page;
        this.isLast = isLast;
    }

    public static ConsultationResponses of(final List<ConsultationResponse> responses,
                                           final List<Consultation> consultations,
                                           final Slice<Consultation> allConsultations) {
        return new ConsultationResponses(responses, consultations.size(),
                allConsultations.getPageable().getPageNumber(), allConsultations.isLast());
    }


}
