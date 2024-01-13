package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class ClassLogRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
    private boolean isAllDay;
    private List<MultipartFile> classLogImages;

    public static ClassLogRequestDto of(String title,
                                        LocalDateTime startDate,
                                        LocalDateTime endDate,
                                        String plan,
                                        String classContents,
                                        String submission,
                                        String magnitude,
                                        boolean isAllDay,
                                        List<MultipartFile> classLogImages) {
        return ClassLogRequestDto.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .plan(plan)
                .classContents(classContents)
                .submission(submission)
                .magnitude(magnitude)
                .isAllDay(isAllDay)
                .classLogImages(classLogImages)
                .build();
    }
}