package com.example.jobapplication.ratelimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

/**
 * Applies fixed-window rate limits using Redis atomic increments.
 */
@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;

    public RateLimitService(StringRedisTemplate redisTemplate, RateLimitProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public RateLimitDecision checkLimit(String clientId, String httpMethod) {
        long limit = resolveLimit(httpMethod);
        String bucket = isWriteMethod(httpMethod) ? "write" : "read";
        return checkLimit(clientId, bucket, limit);
    }

    public RateLimitDecision checkLimit(String clientId, String bucket, long limit) {
        if (!properties.isEnabled()) {
            return new RateLimitDecision(true, Long.MAX_VALUE, Long.MAX_VALUE, 0);
        }

        if (limit <= 0) {
            return new RateLimitDecision(true, Long.MAX_VALUE, Long.MAX_VALUE, 0);
        }

        long windowSeconds = Math.max(properties.getWindowSeconds(), 1);
        long now = Instant.now().getEpochSecond();
        long windowIndex = now / windowSeconds;
        String key = "rate-limit:" + clientId + ":" + bucket + ":" + windowIndex;

        try {
            Long current = redisTemplate.opsForValue().increment(key);
            if (current != null && current == 1L) {
                redisTemplate.expire(key, Duration.ofSeconds(windowSeconds + 1));
            }

            long used = current == null ? 0 : current;
            boolean allowed = used <= limit;
            long remaining = Math.max(limit - used, 0);
            long retryAfter = allowed ? 0 : Math.max(windowSeconds - (now % windowSeconds), 1);

            return new RateLimitDecision(allowed, limit, remaining, retryAfter);
        } catch (Exception ignored) {
            // Fail open so API remains available if Redis is temporarily unavailable.
            return new RateLimitDecision(true, limit, Math.max(limit - 1, 0), 0);
        }
    }

    private long resolveLimit(String httpMethod) {
        return isWriteMethod(httpMethod) ? properties.getWriteRequests() : properties.getReadRequests();
    }

    private boolean isWriteMethod(String httpMethod) {
        String method = httpMethod == null ? "" : httpMethod.toUpperCase(Locale.ROOT);
        return "POST".equals(method)
                || "PUT".equals(method)
                || "PATCH".equals(method)
                || "DELETE".equals(method);
    }
}
