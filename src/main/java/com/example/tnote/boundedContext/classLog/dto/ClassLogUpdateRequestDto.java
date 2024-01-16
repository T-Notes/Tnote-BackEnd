package com.example.tnote.boundedContext.classLog.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ClassLogUpdateRequestDto {
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public boolean hasPlan() {
        return plan != null;
    }

    public boolean hasClassContents() {
        return classContents != null;
    }

    public boolean hasSubmission() {
        return submission != null;
    }

    public boolean hasMagnitude() {
        return magnitude != null;
    }

}
