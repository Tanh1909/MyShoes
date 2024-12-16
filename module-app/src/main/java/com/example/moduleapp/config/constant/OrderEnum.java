package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderEnum {
    PENDING("PENDING"),
    PAYMENT_CONFIRMED("PAYMENT_CONFIRMED"),
    CANCEL("CANCEL"),
    SHIPPING("SHIPPING"),
    DELIVERED("DELIVERED"),
    SUCCESS("SUCCESS"),
    REFUND("REFUND");
    private final String value;

    public static OrderEnum getValue(String value) {
        try {
            return OrderEnum.valueOf(value);
        } catch (Exception e) {
            throw new AppException(ErrorCodeBase.NOT_FOUND, "ORDER STATUS");
        }
    }

    public static OrderEnum fromValue(String value) {
        try {
            return OrderEnum.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

}
