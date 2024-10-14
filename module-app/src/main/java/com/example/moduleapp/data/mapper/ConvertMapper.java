package com.example.moduleapp.data.mapper;

import org.mapstruct.Mapper;

@Mapper
public interface ConvertMapper {
    default boolean intToBoolean(int value) {
        return value != 0;
    }

    default int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
