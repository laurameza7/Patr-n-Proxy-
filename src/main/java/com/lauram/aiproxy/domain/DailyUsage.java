package com.lauram.aiproxy.domain;

import java.time.LocalDate;

/**
 * Consumption registered by a user on a single day.
 */
public class DailyUsage {

    private final LocalDate date;
    private long tokens;
    private int requests;

    public DailyUsage(LocalDate date) {
        this.date = date;
    }

    void register(long consumedTokens) {
        this.tokens += consumedTokens;
        this.requests++;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getTokens() {
        return tokens;
    }

    public int getRequests() {
        return requests;
    }
}
