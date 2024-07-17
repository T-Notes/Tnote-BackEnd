package com.example.tnote.base.exception;

import lombok.Getter;

@Getter
public class CustomExceptions extends RuntimeException {

    private final ErrorCodes errorCode;

    public static final CustomExceptions CLASS_LOG_NOT_FOUNT = new CustomExceptions(ErrorCodes.CLASS_LOG_NOT_FOUNT);
    public static final CustomExceptions INVALID_CLASS_LOG_DATA = new CustomExceptions(ErrorCodes.INVALID_CLASS_LOG_DATA);
    public static final CustomExceptions INVALID_CLASS_LOG_DATE = new CustomExceptions(ErrorCodes.INVALID_CLASS_LOG_DATE);
    public static final CustomExceptions CONSULTATION_NOT_FOUNT = new CustomExceptions(ErrorCodes.CONSULTATION_NOT_FOUNT);
    public static final CustomExceptions INVALID_CONSULTATION_DATA = new CustomExceptions(
            ErrorCodes.INVALID_CONSULTATION_DATA);
    public static final CustomExceptions INVALID_CONSULTATION_DATE = new CustomExceptions(
            ErrorCodes.INVALID_CONSULTATION_DATE);
    public static final CustomExceptions INVALID_COUNSELING_TYPE = new CustomExceptions(
            ErrorCodes.INVALID_COUNSELING_TYPE);
    public static final CustomExceptions INVALID_COUNSELING_FIELD = new CustomExceptions(
            ErrorCodes.INVALID_COUNSELING_FIELD);
    public static final CustomExceptions OBSERVATION_NOT_FOUNT = new CustomExceptions(ErrorCodes.OBSERVATION_NOT_FOUNT);
    public static final CustomExceptions INVALID_OBSERVATION_DATA = new CustomExceptions(
            ErrorCodes.INVALID_OBSERVATION_DATA);
    public static final CustomExceptions INVALID_OBSERVATION_DATE = new CustomExceptions(
            ErrorCodes.INVALID_OBSERVATION_DATE);
    public static final CustomExceptions PROCEEDING_NOT_FOUNT = new CustomExceptions(ErrorCodes.PROCEEDING_NOT_FOUNT);
    public static final CustomExceptions INVALID_PROCEEDING_DATA = new CustomExceptions(
            ErrorCodes.INVALID_PROCEEDING_DATA);
    public static final CustomExceptions INVALID_PROCEEDING_DATE = new CustomExceptions(
            ErrorCodes.INVALID_PROCEEDING_DATE);
    public static final CustomExceptions BAD_REQUEST = new CustomExceptions(ErrorCodes.BAD_REQUEST);
    public static final CustomExceptions UNAUTHORIZED = new CustomExceptions(ErrorCodes.UNAUTHORIZED);
    public static final CustomExceptions WRONG_TOKEN = new CustomExceptions(ErrorCodes.WRONG_TOKEN);
    public static final CustomExceptions WRONG_REFRESH_TOKEN = new CustomExceptions(ErrorCodes.WRONG_REFRESH_TOKEN);
    public static final CustomExceptions WRONG_SIGNATURE = new CustomExceptions(ErrorCodes.WRONG_SIGNATURE);
    public static final CustomExceptions UNSUPPORTED = new CustomExceptions(ErrorCodes.UNSUPPORTED);
    public static final CustomExceptions EXPIRED_REFRESH_TOKEN = new CustomExceptions(ErrorCodes.EXPIRED_REFRESH_TOKEN);
    public static final CustomExceptions EXPIRED_ACCESS_TOKEN = new CustomExceptions(ErrorCodes.EXPIRED_ACCESS_TOKEN);
    public static final CustomExceptions NOT_EXPIRED_TOKEN = new CustomExceptions(ErrorCodes.NOT_EXPIRED_TOKEN);
    public static final CustomExceptions SUBJECT_NOT_FOUND = new CustomExceptions(ErrorCodes.SUBJECT_NOT_FOUND);
    public static final CustomExceptions SCHEDULE_NOT_FOUND = new CustomExceptions(ErrorCodes.SCHEDULE_NOT_FOUND);
    public static final CustomExceptions TODAY_IS_WRONG_WITH_DATE = new CustomExceptions(
            ErrorCodes.TODAY_IS_WRONG_WITH_DATE);
    public static final CustomExceptions TODO_NOT_FOUND = new CustomExceptions(ErrorCodes.TODO_NOT_FOUND);
    public static final CustomExceptions DUPLICATED_NICKNAME = new CustomExceptions(ErrorCodes.DUPLICATED_NICKNAME);
    public static final CustomExceptions NOT_VALID_FIELD = new CustomExceptions(ErrorCodes.NOT_VALID_FIELD);
    public static final CustomExceptions WRONG_EMAIL = new CustomExceptions(ErrorCodes.WRONG_EMAIL);
    public static final CustomExceptions USER_NOT_FOUND = new CustomExceptions(ErrorCodes.USER_NOT_FOUND);
    public static final CustomExceptions WRONG_CLASS_TIME = new CustomExceptions(ErrorCodes.WRONG_CLASS_TIME);


    public CustomExceptions(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
