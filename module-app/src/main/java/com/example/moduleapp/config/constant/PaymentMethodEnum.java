package com.example.moduleapp.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethodEnum {
    CASH("CASH"),
    VNPAY("VNPAY");
    private final String value;

    public static PaymentMethodEnum getValue(String value) {
        try {
            return PaymentMethodEnum.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
}
