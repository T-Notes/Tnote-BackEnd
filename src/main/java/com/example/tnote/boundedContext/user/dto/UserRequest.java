package com.example.tnote.boundedContext.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserRequest {

    private String email;
    private String name;
    private String region;
    private String gubun;
    private String schoolName;
    private String subject;
    private int career;
    private boolean alarm;

}
