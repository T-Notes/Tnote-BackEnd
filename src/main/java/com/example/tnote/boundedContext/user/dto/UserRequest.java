package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserRequest {

    private String email;
    private String name;
    private String schoolType;
    private String code; // 시도행정코드
    private String schoolName;
    private String scheduleCode; // 행정표준코드
    private String subject;
    private int career;
    private boolean alarm;

}
