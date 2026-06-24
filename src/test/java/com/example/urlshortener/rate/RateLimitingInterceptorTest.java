package com.example.urlshortener.rate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.urlshortener.config.RateLimitProperties;
import com.example.urlshortener.exception.RateLimitExceededException;

class RateLimitingInterceptorTest {

    private RateLimitingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setEnabled(true);
        properties.setMaxRequests(2);
        properties.setWindow(Duration.ofMinutes(1));

        RateLimitingService rateLimitingService = new RateLimitingService(properties);
        rateLimitingService.validateConfiguration();
        interceptor = new RateLimitingInterceptor(rateLimitingService);
    }

    @Test
    void preHandleShouldRejectRequestsAboveTheConfiguredLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/shorten");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
        assertThrows(RateLimitExceededException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void preHandleShouldSkipExcludedDocumentationRoutes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/swagger-ui/index.html");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertDoesNotThrow(() -> interceptor.preHandle(request, response, new Object()));
    }
}
