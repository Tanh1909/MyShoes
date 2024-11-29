package com.example.moduleapp.config.constant;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderItemEnum {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    CANCEL("CANCEL");
    private final String value;

    public static OrderEnum getValue(String value) {
        try {
            return OrderEnum.valueOf(value);
        } catch (Exception e) {
            throw new AppException(ErrorCodeBase.NOT_FOUND, "ORDER STATUS");
        }
    }

}
