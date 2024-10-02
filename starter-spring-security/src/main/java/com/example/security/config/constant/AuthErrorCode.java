package com.example.security.config.constant;

import com.example.common.exception.ErrorCode;
import com.example.common.config.constant.ErrorCodeBase;
import org.springframework.http.HttpStatus;

public interface AuthErrorCode extends ErrorCodeBase {
    ErrorCode UNAUTHENTICATED = ErrorCode.builder().code(1011).message("YOU NEED TO LOGIN!").httpStatus(HttpStatus.UNAUTHORIZED).build();
    ErrorCode UNAUTHORIZED = ErrorCode.builder().code(1011).message("YOU NEED PERMISSION!").httpStatus(HttpStatus.FORBIDDEN).build();
    ErrorCode WRONG_USERNAME_OR_PASSWORD = ErrorCode.builder().code(1011).message("WRONG USERNAME OR PASSWORD!").httpStatus(HttpStatus.UNAUTHORIZED).build();
}
