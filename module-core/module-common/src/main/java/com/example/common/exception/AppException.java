package com.example.common.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode,String ...arg) {
        super(errorCode.getMessage(arg));
        this.errorCode=errorCode;
    }
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }
}
