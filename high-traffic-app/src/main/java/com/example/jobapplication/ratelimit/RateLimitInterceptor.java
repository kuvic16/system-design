package com.example.jobapplication.ratelimit;

import com.example.jobapplication.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Enforces method-level rate limits for endpoints annotated with {@link RateLimited}.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final Environment environment;

    public RateLimitInterceptor(RateLimitService rateLimitService, Environment environment) {
        this.rateLimitService = rateLimitService;
        this.environment = environment;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimited rateLimited = resolveAnnotation(handlerMethod);
        if (rateLimited == null) {
            return true;
        }

        String clientId = extractClientId(request);
        long limit = resolveLimit(rateLimited);
        RateLimitDecision decision = rateLimitService.checkLimit(clientId, rateLimited.bucket(), limit);

        response.setHeader("X-RateLimit-Limit", String.valueOf(decision.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(decision.remaining()));

        if (!decision.allowed()) {
            response.setHeader("Retry-After", String.valueOf(decision.retryAfterSeconds()));
            throw new RateLimitExceededException(decision.limit(), decision.remaining(), decision.retryAfterSeconds());
        }

        return true;
    }

    private RateLimited resolveAnnotation(HandlerMethod handlerMethod) {
        RateLimited methodAnnotation = handlerMethod.getMethodAnnotation(RateLimited.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return handlerMethod.getBeanType().getAnnotation(RateLimited.class);
    }

    private long resolveLimit(RateLimited rateLimited) {
        if (rateLimited.limit() > 0) {
            return rateLimited.limit();
        }

        if (!rateLimited.limitProperty().isBlank()) {
            return environment.getProperty(rateLimited.limitProperty(), Long.class, -1L);
        }

        return -1L;
    }

    private String extractClientId(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String[] parts = forwardedFor.split(",");
            return parts[0].trim();
        }
        return request.getRemoteAddr();
    }
}
