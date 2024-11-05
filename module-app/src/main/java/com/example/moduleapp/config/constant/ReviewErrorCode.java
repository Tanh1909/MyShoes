package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class ReviewErrorCode implements ErrorCodeBase {
    public static final ErrorCode HAS_REVIEWED = ErrorCode.builder().httpStatus(HttpStatus.BAD_REQUEST).code(1010).message("YOU HAVE REVIEWED").build();
}
