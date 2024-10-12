package com.example.moduleapp.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentEnum {
    PENDING("PENDING"),
    SUCCESS("SUCCESS");
    private final String value;
}
