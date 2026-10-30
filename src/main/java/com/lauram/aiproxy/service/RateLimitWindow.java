package com.lauram.aiproxy.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Describes the fixed one-minute window used by the rate limiter.
 * Windows are aligned to the start of every minute, matching the scheduled reset.
 */
@Component
public class RateLimitWindow {

    private static final int WINDOW_SECONDS = 60;

    private final Clock clock;

    public RateLimitWindow(Clock clock) {
        this.clock = clock;
    }

    public long secondsUntilReset() {
        int currentSecond = LocalDateTime.now(clock).getSecond();
        return WINDOW_SECONDS - currentSecond;
    }
}
