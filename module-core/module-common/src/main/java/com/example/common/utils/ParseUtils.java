package com.example.common.utils;

public class ParseUtils {
    public static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer parseIntOrDefault(String str, Integer defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
