package com.example.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final String VIETNAM_DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final String TIME_DATE_PATTERN = "HH:mm:ss yyyy-MM-dd";

    public static String getFormat(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }
}
