package com.example.moduleapp.config.constant;

import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AddressErrorCode {
    public static final ErrorCode USER_NOT_HAS_THIS_ADDRESS = ErrorCode.builder().code(1000).httpStatus(HttpStatus.BAD_REQUEST).message("USER NOT HAVE THIS ADDRESS").build();

}
