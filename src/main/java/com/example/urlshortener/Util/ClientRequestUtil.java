package com.example.urlshortener.util;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientRequestUtil {

    private ClientRequestUtil() {
    }

    public static String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }

        if (StringUtils.hasText(request.getRemoteAddr())) {
            return request.getRemoteAddr().trim();
        }

        return "unknown-client";
    }
}
