package com.example.jobapplication.exception;

/**
 * Raised when a request exceeds the configured rate limit.
 */
public class RateLimitExceededException extends RuntimeException {

    private final long limit;
    private final long remaining;
    private final long retryAfterSeconds;

    public RateLimitExceededException(long limit, long remaining, long retryAfterSeconds) {
        super("Rate limit exceeded");
        this.limit = limit;
        this.remaining = remaining;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getLimit() {
        return limit;
    }

    public long getRemaining() {
        return remaining;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
