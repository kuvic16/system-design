package com.example.jobapplication.ratelimit;

/**
 * Result of a rate limit check.
 */
public record RateLimitDecision(boolean allowed, long limit, long remaining, long retryAfterSeconds) {
}
