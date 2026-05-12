package com.example.jobapplication.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized configuration for API rate limits.
 */
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;
    private long windowSeconds = 60;
    private long getAllRequests = 30;
    private long readRequests = 120;
    private long writeRequests = 60;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(long windowSeconds) {
        this.windowSeconds = windowSeconds;
    }

    public long getGetAllRequests() {
        return getAllRequests;
    }

    public void setGetAllRequests(long getAllRequests) {
        this.getAllRequests = getAllRequests;
    }

    public long getReadRequests() {
        return readRequests;
    }

    public void setReadRequests(long readRequests) {
        this.readRequests = readRequests;
    }

    public long getWriteRequests() {
        return writeRequests;
    }

    public void setWriteRequests(long writeRequests) {
        this.writeRequests = writeRequests;
    }
}
