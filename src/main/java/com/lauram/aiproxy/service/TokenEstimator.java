package com.lauram.aiproxy.service;

import org.springframework.stereotype.Component;

/**
 * Approximates token count using the common heuristic of ~4 characters per token.
 */
@Component
public class TokenEstimator {

    private static final double CHARACTERS_PER_TOKEN = 4.0;

    public long estimate(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return (long) Math.ceil(text.length() / CHARACTERS_PER_TOKEN);
    }

    public long estimateRequestCost(String prompt, int maxTokens) {
        return estimate(prompt) + maxTokens;
    }
}
