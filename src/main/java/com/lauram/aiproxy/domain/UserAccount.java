package com.lauram.aiproxy.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * Aggregate that holds the consumption state of a single user:
 * current plan, requests in the current minute window, monthly tokens and daily history.
 */
public class UserAccount {

    private final String userId;
    private PlanType plan;
    private int requestsInCurrentWindow;
    private long tokensUsedThisMonth;
    private final Map<LocalDate, DailyUsage> dailyUsage = new TreeMap<>();

    public UserAccount(String userId, PlanType plan) {
        this.userId = userId;
        this.plan = plan;
    }

    public synchronized boolean tryAcquireRequestSlot() {
        if (plan.hasUnlimitedRequests() || requestsInCurrentWindow < plan.getRequestsPerMinute()) {
            requestsInCurrentWindow++;
            return true;
        }
        return false;
    }

    public synchronized void resetRateWindow() {
        requestsInCurrentWindow = 0;
    }

    public synchronized boolean hasTokensFor(long tokens) {
        return plan.hasUnlimitedTokens() || getRemainingTokens() >= tokens;
    }

    public synchronized void consumeTokens(long tokens, LocalDate day) {
        tokensUsedThisMonth += tokens;
        dailyUsage.computeIfAbsent(day, DailyUsage::new).register(tokens);
    }

    public synchronized void resetMonthlyQuota() {
        tokensUsedThisMonth = 0;
    }

    public synchronized void changePlan(PlanType newPlan) {
        this.plan = newPlan;
    }

    public synchronized long getRemainingTokens() {
        if (plan.hasUnlimitedTokens()) {
            return -1;
        }
        return Math.max(0, plan.getMonthlyTokens() - tokensUsedThisMonth);
    }

    public synchronized DailyUsage getUsageFor(LocalDate day) {
        DailyUsage usage = dailyUsage.get(day);
        return usage != null ? usage : new DailyUsage(day);
    }

    public String getUserId() {
        return userId;
    }

    public synchronized PlanType getPlan() {
        return plan;
    }

    public synchronized int getRequestsInCurrentWindow() {
        return requestsInCurrentWindow;
    }

    public synchronized long getTokensUsedThisMonth() {
        return tokensUsedThisMonth;
    }
}
