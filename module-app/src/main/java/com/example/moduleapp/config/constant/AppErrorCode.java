package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class AppErrorCode implements ErrorCodeBase {
    public static final ErrorCode USER_NOT_HAS_THIS_ADDRESS = ErrorCode.builder().code(1000).httpStatus(HttpStatus.BAD_REQUEST).message("USER NOT HAVE THIS ADDRESS").build();
    public static final ErrorCode WRONG_BUSINESS_UPDATE_STATUS = ErrorCode.builder().httpStatus(HttpStatus.BAD_REQUEST).code(1010).message("WRONG BUSINESS UPDATE STATUS").build();
    public static final ErrorCode OVER_STOCK = ErrorCode.builder().httpStatus(HttpStatus.BAD_REQUEST).code(1010).message("OVER STOCK").build();
    public static final ErrorCode ORDER_HAS_BEEN_PAYED = ErrorCode.builder().code(1111).message("Your order has been paid!").httpStatus(HttpStatus.BAD_REQUEST).build();
    public static final ErrorCode ORDER_HAS_NOT_BEEN_PAYED = ErrorCode.builder().code(1111).message("Your order has not been paid yet!").httpStatus(HttpStatus.BAD_REQUEST).build();
    public static final ErrorCode HAS_REVIEWED = ErrorCode.builder().httpStatus(HttpStatus.BAD_REQUEST).code(1010).message("YOU HAVE REVIEWED").build();
    public static final ErrorCode PAYMENT_METHOD_NOT_SUPPORT = ErrorCode.builder().code(1111).message("Payment method not supported!").httpStatus(HttpStatus.BAD_REQUEST).build();
}
