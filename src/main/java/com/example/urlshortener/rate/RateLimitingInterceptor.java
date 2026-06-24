package com.example.urlshortener.rate;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.urlshortener.util.ClientRequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    public RateLimitingInterceptor(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!shouldRateLimit(request)) {
            return true;
        }

        rateLimitingService.validateRequest(ClientRequestUtil.extractClientIp(request));
        return true;
    }

    private boolean shouldRateLimit(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return requestUri != null
                && !requestUri.startsWith("/swagger-ui")
                && !requestUri.startsWith("/v3/api-docs")
                && !requestUri.equals("/swagger-ui.html")
                && !requestUri.equals("/error");
    }
}
