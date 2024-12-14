package com.example.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.Map;

@Log4j2
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static ObjectMapper getObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    public static String encode(Object obj) {
        try {

            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Error converting Object to JSON: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T decode(String json, Class<T> type) {
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            log.error("Error converting JSON to Object: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T decode(String json, TypeReference<T> type) {
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            log.error("Error converting JSON to TypeReference: {}", e.getMessage());
            return null;
        }
    }

    public static <K, V> Map<K, V> covertObjToMap(Object obj, Class<K> key, Class<V> value) {
        try {
            return getObjectMapper().convertValue(obj, new TypeReference<Map<K, V>>() {
            });
        } catch (Exception e) {
            log.error("Error converting Object to Map: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

}
