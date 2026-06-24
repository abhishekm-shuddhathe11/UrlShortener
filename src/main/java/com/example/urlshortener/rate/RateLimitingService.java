package com.example.urlshortener.rate;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.urlshortener.config.RateLimitProperties;
import com.example.urlshortener.exception.RateLimitExceededException;

import jakarta.annotation.PostConstruct;

@Service
public class RateLimitingService {

    private static final long CLEANUP_INTERVAL = 256L;

    private final RateLimitProperties properties;
    private final Map<String, RateLimitWindow> clientWindows = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong();

    public RateLimitingService(RateLimitProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void validateConfiguration() {
        if (properties.getMaxRequests() < 1) {
            throw new IllegalStateException("app.rate-limit.max-requests must be greater than zero");
        }
        if (properties.getWindow() == null || properties.getWindow().isZero() || properties.getWindow().isNegative()) {
            throw new IllegalStateException("app.rate-limit.window must be a positive duration");
        }
    }

    public void validateRequest(String clientId) {
        if (!properties.isEnabled()) {
            return;
        }
        if (!StringUtils.hasText(clientId)) {
            throw new IllegalArgumentException("Client identifier must not be blank");
        }

        Instant now = Instant.now();
        Duration window = properties.getWindow();
        AtomicReference<RateLimitDecision> decision = new AtomicReference<>(RateLimitDecision.allowed());

        clientWindows.compute(clientId, (key, currentWindow) -> {
            if (currentWindow == null || currentWindow.isExpired(now, window)) {
                return new RateLimitWindow(now, 1);
            }

            if (currentWindow.requestCount() >= properties.getMaxRequests()) {
                decision.set(RateLimitDecision.rejected(secondsUntilReset(now, currentWindow.windowStartedAt(), window)));
                return currentWindow;
            }

            return currentWindow.increment();
        });

        cleanupExpiredWindowsIfNeeded(now, window);

        if (decision.get().rejected()) {
            throw new RateLimitExceededException(decision.get().retryAfterSeconds());
        }
    }

    private void cleanupExpiredWindowsIfNeeded(Instant now, Duration window) {
        long currentCount = requestCounter.incrementAndGet();
        if (currentCount % CLEANUP_INTERVAL != 0) {
            return;
        }

        clientWindows.entrySet().removeIf(entry -> entry.getValue().isExpired(now, window));
    }

    private long secondsUntilReset(Instant now, Instant windowStart, Duration window) {
        Instant resetTime = windowStart.plus(window);
        long retryAfter = Duration.between(now, resetTime).toSeconds();
        return Math.max(1L, retryAfter);
    }

    private record RateLimitWindow(Instant windowStartedAt, int requestCount) {

        private boolean isExpired(Instant now, Duration window) {
            return !now.isBefore(windowStartedAt.plus(window));
        }

        private RateLimitWindow increment() {
            return new RateLimitWindow(windowStartedAt, requestCount + 1);
        }
    }

    private record RateLimitDecision(boolean rejected, long retryAfterSeconds) {

        private static RateLimitDecision allowed() {
            return new RateLimitDecision(false, 0L);
        }

        private static RateLimitDecision rejected(long retryAfterSeconds) {
            return new RateLimitDecision(true, retryAfterSeconds);
        }
    }
}
