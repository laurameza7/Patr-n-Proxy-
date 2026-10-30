package com.lauram.aiproxy.dto;

import java.time.Instant;

/**
 * Result produced by the AI service. Proxies can enrich it with quota data.
 */
public record GenerationResponse(
        String text,
        long promptTokens,
        long completionTokens,
        long totalTokens,
        long processingTimeMs,
        Instant generatedAt,
        QuotaStatusResponse quota) {

    public GenerationResponse withQuota(QuotaStatusResponse quotaStatus) {
        return new GenerationResponse(text, promptTokens, completionTokens, totalTokens,
                processingTimeMs, generatedAt, quotaStatus);
    }
}
