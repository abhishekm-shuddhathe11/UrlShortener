package com.example.urlshortener.util;

public class Base62 {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public static String encode(long num) {
        if (num == 0) {
            return String.valueOf(CHARSET.charAt(0)); // Return 'a' for 0
        }

        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            sb.append(CHARSET.charAt((int)(num % 62)));
            num /= 62;
        }

        return sb.reverse().toString();
    }
}