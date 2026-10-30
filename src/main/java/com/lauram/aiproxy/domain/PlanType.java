package com.lauram.aiproxy.domain;

/**
 * Subscription plans with their request-per-minute and monthly-token limits.
 * A negative limit means "unlimited".
 */
public enum PlanType {
    FREE(10, 50_000),
    PRO(60, 500_000),
    ENTERPRISE(-1, -1);

    private static final int UNLIMITED = -1;

    private final int requestsPerMinute;
    private final long monthlyTokens;

    PlanType(int requestsPerMinute, long monthlyTokens) {
        this.requestsPerMinute = requestsPerMinute;
        this.monthlyTokens = monthlyTokens;
    }

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public long getMonthlyTokens() {
        return monthlyTokens;
    }

    public boolean hasUnlimitedRequests() {
        return requestsPerMinute == UNLIMITED;
    }

    public boolean hasUnlimitedTokens() {
        return monthlyTokens == UNLIMITED;
    }

    public boolean canUpgradeTo(PlanType target) {
        return target.ordinal() > this.ordinal();
    }
}
