package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class OrderErrorCode implements ErrorCodeBase {
    public static final ErrorCode WRONG_BUSINESS_UPDATE_STATUS = ErrorCode.builder().httpStatus(HttpStatus.BAD_REQUEST).code(1010).message("WRONG BUSINESS UPDATE STATUS").build();
}
