package com.example.tnote.base.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public static final CustomException CLASS_LOG_NOT_FOUNT = new CustomException(ErrorCode.CLASS_LOG_NOT_FOUNT);
    public static final CustomException INVALID_CLASS_LOG_DATA = new CustomException(ErrorCode.INVALID_CLASS_LOG_DATA);
    public static final CustomException INVALID_CLASS_LOG_DATE = new CustomException(ErrorCode.INVALID_CLASS_LOG_DATE);
    public static final CustomException CONSULTATION_NOT_FOUNT = new CustomException(ErrorCode.CONSULTATION_NOT_FOUNT);
    public static final CustomException INVALID_CONSULTATION_DATA = new CustomException(
            ErrorCode.INVALID_CONSULTATION_DATA);
    public static final CustomException INVALID_CONSULTATION_DATE = new CustomException(ErrorCode.INVALID_CONSULTATION_DATE);
    public static final CustomException INVALID_COUNSELING_TYPE = new CustomException(
            ErrorCode.INVALID_COUNSELING_TYPE);
    public static final CustomException INVALID_COUNSELING_FIELD = new CustomException(
            ErrorCode.INVALID_COUNSELING_FIELD);
    public static final CustomException OBSERVATION_NOT_FOUNT = new CustomException(ErrorCode.OBSERVATION_NOT_FOUNT);
    public static final CustomException INVALID_OBSERVATION_DATA = new CustomException(
            ErrorCode.INVALID_OBSERVATION_DATA);
    public static final CustomException INVALID_OBSERVATION_DATE = new CustomException(ErrorCode.INVALID_OBSERVATION_DATE);
    public static final CustomException PROCEEDING_NOT_FOUNT = new CustomException(ErrorCode.PROCEEDING_NOT_FOUNT);
    public static final CustomException INVALID_PROCEEDING_DATA = new CustomException(
            ErrorCode.INVALID_PROCEEDING_DATA);
    public static final CustomException INVALID_PROCEEDING_DATE = new CustomException(ErrorCode.INVALID_PROCEEDING_DATE);
    public static final CustomException BAD_REQUEST = new CustomException(ErrorCode.BAD_REQUEST);
    public static final CustomException UNAUTHORIZED = new CustomException(ErrorCode.UNAUTHORIZED);
    public static final CustomException WRONG_TOKEN = new CustomException(ErrorCode.WRONG_TOKEN);
    public static final CustomException WRONG_REFRESH_TOKEN = new CustomException(ErrorCode.WRONG_REFRESH_TOKEN);
    public static final CustomException WRONG_SIGNATURE = new CustomException(ErrorCode.WRONG_SIGNATURE);
    public static final CustomException UNSUPPORTED = new CustomException(ErrorCode.UNSUPPORTED);
    public static final CustomException EXPIRED_REFRESH_TOKEN = new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    public static final CustomException EXPIRED_ACCESS_TOKEN = new CustomException(ErrorCode.EXPIRED_ACCESS_TOKEN);
    public static final CustomException NOT_EXPIRED_TOKEN = new CustomException(ErrorCode.NOT_EXPIRED_TOKEN);
    public static final CustomException SUBJECT_NOT_FOUND = new CustomException(ErrorCode.SUBJECT_NOT_FOUND);
    public static final CustomException SCHEDULE_NOT_FOUND = new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
    public static final CustomException TODAY_IS_WRONG_WITH_DATE = new CustomException(
            ErrorCode.TODAY_IS_WRONG_WITH_DATE);
    public static final CustomException TODO_NOT_FOUND = new CustomException(ErrorCode.TODO_NOT_FOUND);
    public static final CustomException DUPLICATED_NICKNAME = new CustomException(ErrorCode.DUPLICATED_NICKNAME);
    public static final CustomException NOT_VALID_FIELD = new CustomException(ErrorCode.NOT_VALID_FIELD);
    public static final CustomException WRONG_EMAIL = new CustomException(ErrorCode.WRONG_EMAIL);
    public static final CustomException USER_NOT_FOUND = new CustomException(ErrorCode.USER_NOT_FOUND);


    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
