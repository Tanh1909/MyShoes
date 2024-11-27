package com.example.common.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ErrorCode {
    private HttpStatus httpStatus;
    private int code;
    private String message;

    public String getMessage(String... args) {
        this.message = String.format(message, args);
        return message;
    }

}
