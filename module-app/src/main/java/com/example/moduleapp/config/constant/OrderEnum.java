package com.example.moduleapp.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderEnum {
    PENDING("PENDING"),
    PAYMENT_CONFIRM("PAYMENT_CONFIRM"),
    CANCEL("CANCEL"),
    SHIPPING("SHIPPING"),
    DELIVERED("DELIVERED"),
    SUCCESS("SUCCESS");
    private final String value;

    public static OrderEnum getOrderEnum(String value) {
        return OrderEnum.valueOf(value);
    }

}
