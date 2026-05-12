package com.example.jobapplication.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Filters API requests and blocks calls that exceed configured limits.
 */
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String JOB_APPLICATION_LIST_PATH = "/api/job-applications";

    private final RateLimitService rateLimitService;
    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimitService rateLimitService, RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        this.rateLimitService = rateLimitService;
        this.rateLimitProperties = rateLimitProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"GET"
                .equalsIgnoreCase(request.getMethod())
                || !JOB_APPLICATION_LIST_PATH.equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String clientId = extractClientId(request);
        RateLimitDecision decision = rateLimitService.checkLimit(
                clientId,
                "job-applications:list",
                rateLimitProperties.getGetAllRequests());

        response.setHeader("X-RateLimit-Limit", String.valueOf(decision.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(decision.remaining()));

        if (!decision.allowed()) {
            int tooManyRequests = HttpStatus.TOO_MANY_REQUESTS.value();
            response.setStatus(tooManyRequests);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(decision.retryAfterSeconds()));

            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", tooManyRequests);
            body.put("message", "Rate limit exceeded");
            body.put("retryAfterSeconds", decision.retryAfterSeconds());

            response.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }

        filterChain.doFilter(request, response);
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
