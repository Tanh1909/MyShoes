package com.example.common.config.constant;

import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public interface ErrorCodeBase {
    ErrorCode INTERNAL_SERVER_ERROR = ErrorCode.builder().message("SOMETHING IS WRONG!").code(1000).httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build();
    ErrorCode BAD_REQUEST = ErrorCode.builder().message("%s").code(1001).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode ALREADY_EXISTS = ErrorCode.builder().message("%s ALREADY EXISTS!").code(1001).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode NOT_FOUND = ErrorCode.builder().message("%s NOT FOUND").code(1002).httpStatus(HttpStatus.NOT_FOUND).build();
    ErrorCode IS_NULL = ErrorCode.builder().message("%s IS NULL ").code(1002).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode IS_NOT_SUPPORTED = ErrorCode.builder().message("%s IS NOT SUPPORTED ").code(1002).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode INVALID_SORT_DIRECTION = ErrorCode.builder().message("SORT DIRECTION %s IS NOT SUPPORTED ").code(1002).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode INVALID_SORT_BY = ErrorCode.builder().message("SORT BY %s IS INVALID ").code(1002).httpStatus(HttpStatus.BAD_REQUEST).build();
    ErrorCode INVALID_SORT_PARAMETER = ErrorCode.builder().message("SORT PARAMETER %s IS INVALID ").code(1002).httpStatus(HttpStatus.BAD_REQUEST).build();


}
