package com.example.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error converting Object to JSON: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("Error converting JSON to Object: {}", e.getMessage());
            return null;
        }
    }
}
