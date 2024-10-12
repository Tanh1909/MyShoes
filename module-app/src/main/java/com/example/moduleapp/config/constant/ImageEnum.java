package com.example.moduleapp.config.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageEnum {
    PRODUCT("PRODUCT"),
    USER("USER"),
    REVIEW("REVIEW");
    private final String value;

    public static ImageEnum from(String value) {
        try {
            return ImageEnum.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
}
