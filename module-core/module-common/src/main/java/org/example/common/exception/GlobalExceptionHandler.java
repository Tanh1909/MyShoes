package org.example.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.data.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse exception(Exception e) {
        ErrorCode errorCode = ErrorCodeBase.INTERNAL_SERVER_ERROR;
        logErrorCode(e);
        return ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
    }

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse appException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        logErrorCode(e);
        return ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
    }

    private static void logErrorCode(Exception e) {
        log.error(e.getClass().getName());
        log.error(e.getMessage());
    }

}
