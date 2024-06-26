package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // class log
    CLASS_LOG_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found classLog"),
    INVALID_CLASS_LOG_DATA(HttpStatus.BAD_REQUEST, "Invalid class log data"),
    INVALID_CLASS_LOG_DATE(HttpStatus.BAD_REQUEST, "ClassLog date must be within the schedule dates"),

    // consultation
    CONSULTATION_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found consultation"),
    INVALID_CONSULTATION_DATA(HttpStatus.BAD_REQUEST, "Invalid consultation log data"),
    INVALID_COUNSELING_TYPE(HttpStatus.BAD_REQUEST, "Invalid counseling type"),
    INVALID_COUNSELING_FIELD(HttpStatus.BAD_REQUEST, "Invalid counseling field"),
    INVALID_CONSULTATION_DATE(HttpStatus.BAD_REQUEST, "Consultation date must be within the schedule dates"),

    // observation
    OBSERVATION_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found observation"),
    INVALID_OBSERVATION_DATA(HttpStatus.BAD_REQUEST, "Invalid observation data"),
    INVALID_OBSERVATION_DATE(HttpStatus.BAD_REQUEST, "Observation date must be within the schedule dates"),

    // proceeding
    PROCEEDING_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found proceeding"),
    INVALID_PROCEEDING_DATE(HttpStatus.BAD_REQUEST, "Proceeding date must be within the schedule dates"),
    INVALID_PROCEEDING_DATA(HttpStatus.BAD_REQUEST, "Invalid proceeding data"),

    // common
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "unauthorized"),

    // jwt
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "wrong token"),
    WRONG_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "wrong refresh token"),
    WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "wrong signature"),
    UNSUPPORTED(HttpStatus.UNAUTHORIZED, "unsupported token"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "expired refresh token"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "expired access token"),
    NOT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "token is not expired"),

    // schedule & subject
    SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "subject is not found"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "subject is not found"),
    TODAY_IS_WRONG_WITH_DATE(HttpStatus.NOT_FOUND, "different date with today"),
    DATES_NOT_INCLUDED_IN_SEMESTER(HttpStatus.NOT_FOUND, "incorrect date in subject"),
    WRONG_CLASS_TIME(HttpStatus.NOT_FOUND, "last class is invalid"),

    // todo
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "todo is not found"),

    // user
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "duplicated nickname"),
    NOT_VALID_FIELD(HttpStatus.BAD_REQUEST, "field is not valid"),
    WRONG_EMAIL(HttpStatus.BAD_REQUEST, "wrong mail"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user is not found"),

    // image
    POST_IMAGE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "This is not a valid file extension."),
    POST_IMAGE_CONVERT_ERROR(HttpStatus.BAD_REQUEST, "No photo available or it has not been converted.");


    private final HttpStatus status;
    private final String message;
}
