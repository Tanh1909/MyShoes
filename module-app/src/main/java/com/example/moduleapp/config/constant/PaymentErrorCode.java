package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class PaymentErrorCode implements ErrorCodeBase {
    public static final ErrorCode PAYMENT_METHOD_NOT_SUPPORT = ErrorCode.builder().code(1111).message("Payment method not supported!").httpStatus(HttpStatus.BAD_REQUEST).build();
    public static final ErrorCode ORDER_HAS_BEEN_PAYED = ErrorCode.builder().code(1111).message("Payment has been paid!").httpStatus(HttpStatus.BAD_REQUEST).build();

}
