package com.lauram.aiproxy.service;

import com.lauram.aiproxy.domain.PlanType;
import com.lauram.aiproxy.domain.UserAccount;
import com.lauram.aiproxy.dto.DailyUsageResponse;
import com.lauram.aiproxy.dto.QuotaStatusResponse;
import com.lauram.aiproxy.dto.UpgradeRequest;
import com.lauram.aiproxy.dto.UpgradeResponse;
import com.lauram.aiproxy.exception.InvalidUpgradeException;
import com.lauram.aiproxy.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Read and management operations over a user's quota: status, 7-day history and plan upgrades.
 */
@Service
public class QuotaService {

    private static final int HISTORY_DAYS = 7;

    private final UserAccountRepository accountRepository;
    private final RateLimitWindow rateLimitWindow;
    private final Clock clock;

    public QuotaService(UserAccountRepository accountRepository, RateLimitWindow rateLimitWindow, Clock clock) {
        this.accountRepository = accountRepository;
        this.rateLimitWindow = rateLimitWindow;
        this.clock = clock;
    }

    public QuotaStatusResponse getStatus(String userId) {
        UserAccount account = accountRepository.findOrCreate(userId);
        PlanType plan = account.getPlan();
        return new QuotaStatusResponse(
                userId,
                plan.name(),
                account.getTokensUsedThisMonth(),
                account.getRemainingTokens(),
                plan.getMonthlyTokens(),
                nextMonthlyResetDate(),
                account.getRequestsInCurrentWindow(),
                plan.getRequestsPerMinute(),
                rateLimitWindow.secondsUntilReset());
    }

    public List<DailyUsageResponse> getHistory(String userId) {
        UserAccount account = accountRepository.findOrCreate(userId);
        LocalDate today = LocalDate.now(clock);
        List<DailyUsageResponse> history = new ArrayList<>();
        for (int daysAgo = HISTORY_DAYS - 1; daysAgo >= 0; daysAgo--) {
            var usage = account.getUsageFor(today.minusDays(daysAgo));
            history.add(new DailyUsageResponse(usage.getDate(), usage.getTokens(), usage.getRequests()));
        }
        return history;
    }

    public UpgradeResponse upgrade(String userId, UpgradeRequest request) {
        UserAccount account = accountRepository.findOrCreate(userId);
        PlanType currentPlan = account.getPlan();
        PlanType targetPlan = request.resolvedTargetPlan();
        if (!currentPlan.canUpgradeTo(targetPlan)) {
            throw new InvalidUpgradeException("No es posible pasar de " + currentPlan + " a " + targetPlan);
        }
        account.changePlan(targetPlan);
        String transactionId = "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new UpgradeResponse(
                "Pago simulado aprobado. Tu plan ahora es " + targetPlan,
                currentPlan.name(),
                transactionId,
                getStatus(userId));
    }

    private LocalDate nextMonthlyResetDate() {
        return LocalDate.now(clock).with(TemporalAdjusters.firstDayOfNextMonth());
    }
}
