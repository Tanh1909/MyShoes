package com.example.common.exception;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentTypeMismatchException.class,
            NoResourceFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse badRequest(Exception e) {
        ErrorCode errorCode = ErrorCodeBase.BAD_REQUEST;
        logErrorCode(e);
        return ApiResponse.error(errorCode.getCode(), errorCode.getMessage(e.getMessage()));
    }

    private static void logErrorCode(Exception e) {
        log.error(e.getClass().getName());
        log.error(e.getMessage());
    }

}
