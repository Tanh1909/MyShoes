package com.example.moduleapp.config.constant;

import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class VNPayErrorCode {
    public static final ErrorCode NOT_SUCCESS = ErrorCode.builder().code(1000).httpStatus(HttpStatus.BAD_REQUEST).message("PAYMENT NOT SUCCESS!").build();
    public static final ErrorCode TIME_OUT = ErrorCode.builder().code(1011).httpStatus(HttpStatus.BAD_REQUEST).message("PAYMENT TIME OUT!").build();
    public static final ErrorCode WRONG_OTP = ErrorCode.builder().code(1013).httpStatus(HttpStatus.BAD_REQUEST).message("WRONG OTP!").build();

}
