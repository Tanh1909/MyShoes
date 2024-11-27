package com.example.common.utils;

import java.security.SecureRandom;

public class StringUtils {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";

    public static String generateRandomString(int size, boolean isUppercase, boolean includeNumbers) {
        // Chọn chuỗi ký tự phù hợp dựa trên `isUppercase`
        String characters = CHARACTERS;

        // Thêm các ký tự số nếu `includeNumbers` là true
        if (includeNumbers) {
            characters += NUMBERS;
        }
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return isUppercase ? result.toString().toUpperCase() : result.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
