package com.example.tnote.base.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCodes errorCode;

    public static final CustomException CLASS_LOG_NOT_FOUNT = new CustomException(ErrorCodes.CLASS_LOG_NOT_FOUNT);
    public static final CustomException INVALID_CLASS_LOG_DATA = new CustomException(ErrorCodes.INVALID_CLASS_LOG_DATA);
    public static final CustomException INVALID_CLASS_LOG_DATE = new CustomException(ErrorCodes.INVALID_CLASS_LOG_DATE);
    public static final CustomException CONSULTATION_NOT_FOUNT = new CustomException(ErrorCodes.CONSULTATION_NOT_FOUNT);
    public static final CustomException INVALID_CONSULTATION_DATA = new CustomException(
            ErrorCodes.INVALID_CONSULTATION_DATA);
    public static final CustomException INVALID_CONSULTATION_DATE = new CustomException(
            ErrorCodes.INVALID_CONSULTATION_DATE);
    public static final CustomException INVALID_COUNSELING_TYPE = new CustomException(
            ErrorCodes.INVALID_COUNSELING_TYPE);
    public static final CustomException INVALID_COUNSELING_FIELD = new CustomException(
            ErrorCodes.INVALID_COUNSELING_FIELD);
    public static final CustomException OBSERVATION_NOT_FOUNT = new CustomException(ErrorCodes.OBSERVATION_NOT_FOUNT);
    public static final CustomException INVALID_OBSERVATION_DATA = new CustomException(
            ErrorCodes.INVALID_OBSERVATION_DATA);
    public static final CustomException INVALID_OBSERVATION_DATE = new CustomException(
            ErrorCodes.INVALID_OBSERVATION_DATE);
    public static final CustomException PROCEEDING_NOT_FOUNT = new CustomException(ErrorCodes.PROCEEDING_NOT_FOUNT);
    public static final CustomException INVALID_PROCEEDING_DATA = new CustomException(
            ErrorCodes.INVALID_PROCEEDING_DATA);
    public static final CustomException INVALID_PROCEEDING_DATE = new CustomException(
            ErrorCodes.INVALID_PROCEEDING_DATE);
    public static final CustomException BAD_REQUEST = new CustomException(ErrorCodes.BAD_REQUEST);
    public static final CustomException UNAUTHORIZED = new CustomException(ErrorCodes.UNAUTHORIZED);
    public static final CustomException WRONG_TOKEN = new CustomException(ErrorCodes.WRONG_TOKEN);
    public static final CustomException WRONG_REFRESH_TOKEN = new CustomException(ErrorCodes.WRONG_REFRESH_TOKEN);
    public static final CustomException WRONG_SIGNATURE = new CustomException(ErrorCodes.WRONG_SIGNATURE);
    public static final CustomException UNSUPPORTED = new CustomException(ErrorCodes.UNSUPPORTED);
    public static final CustomException EXPIRED_REFRESH_TOKEN = new CustomException(ErrorCodes.EXPIRED_REFRESH_TOKEN);
    public static final CustomException EXPIRED_ACCESS_TOKEN = new CustomException(ErrorCodes.EXPIRED_ACCESS_TOKEN);
    public static final CustomException NOT_EXPIRED_TOKEN = new CustomException(ErrorCodes.NOT_EXPIRED_TOKEN);
    public static final CustomException SUBJECT_NOT_FOUND = new CustomException(ErrorCodes.SUBJECT_NOT_FOUND);
    public static final CustomException SCHEDULE_NOT_FOUND = new CustomException(ErrorCodes.SCHEDULE_NOT_FOUND);
    public static final CustomException TODAY_IS_WRONG_WITH_DATE = new CustomException(
            ErrorCodes.TODAY_IS_WRONG_WITH_DATE);
    public static final CustomException TODO_NOT_FOUND = new CustomException(ErrorCodes.TODO_NOT_FOUND);
    public static final CustomException DUPLICATED_NICKNAME = new CustomException(ErrorCodes.DUPLICATED_NICKNAME);
    public static final CustomException NOT_VALID_FIELD = new CustomException(ErrorCodes.NOT_VALID_FIELD);
    public static final CustomException WRONG_EMAIL = new CustomException(ErrorCodes.WRONG_EMAIL);
    public static final CustomException USER_NOT_FOUND = new CustomException(ErrorCodes.USER_NOT_FOUND);
    public static final CustomException WRONG_CLASS_TIME = new CustomException(ErrorCodes.WRONG_CLASS_TIME);


    public CustomException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
