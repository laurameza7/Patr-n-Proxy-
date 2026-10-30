package com.lauram.aiproxy.dto;

import java.time.LocalDate;

/**
 * Snapshot of the user's quota. A value of -1 in a limit means unlimited.
 */
public record QuotaStatusResponse(
        String userId,
        String plan,
        long tokensUsed,
        long tokensRemaining,
        long tokensLimit,
        LocalDate resetDate,
        int requestsUsedThisMinute,
        int requestsLimitPerMinute,
        long secondsUntilRateLimitReset) {
}
