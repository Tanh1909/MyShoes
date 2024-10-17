package com.example.authconfig.config.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
