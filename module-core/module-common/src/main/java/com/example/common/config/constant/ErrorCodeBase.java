package com.example.common.config.constant;

import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public interface ErrorCodeBase {
    ErrorCode INTERNAL_SERVER_ERROR = ErrorCode.builder().message("SOMETHING IS WRONG!").code(1000).httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build();
    ErrorCode BAD_REQUEST = ErrorCode.builder().message("BAD REQUEST").code(1001).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode ALREADY_EXISTS=ErrorCode.builder().message("%s ALREADY EXISTS!").code(1001).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode NOT_FOUND = ErrorCode.builder().message("%s NOT_FOUND").code(1002).httpStatus(HttpStatus.NOT_FOUND).build();
}
