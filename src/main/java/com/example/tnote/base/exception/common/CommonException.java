package com.example.tnote.base.exception.common;

import com.example.tnote.base.exception.common.CommonErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommonException extends RuntimeException{

    private final CommonErrorResult commonErrorResult;
}
