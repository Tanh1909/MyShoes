package com.example.common.utils;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;

import java.util.Optional;

public class ValidateUtils {
    public static <T> T getOptionalValue(Optional<T> entity, Class<T> clazz) {
        if (entity.isEmpty()) {
            String errorMsg = clazz.getSimpleName().toUpperCase() + " ID";
            throw new AppException(ErrorCodeBase.NOT_FOUND, errorMsg);
        }
        return entity.get();
    }
}
