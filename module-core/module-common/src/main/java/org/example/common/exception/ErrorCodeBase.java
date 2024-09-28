package org.example.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCodeBase {
    ErrorCode INTERNAL_SERVER_ERROR = ErrorCode.builder().message("SOMETHING IS WRONG!").code(1000).httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build();
    ErrorCode BAD_REQUEST = ErrorCode.builder().message("BAD REQUEST").code(1001).httpStatus(HttpStatus.BAD_REQUEST).build();
}
