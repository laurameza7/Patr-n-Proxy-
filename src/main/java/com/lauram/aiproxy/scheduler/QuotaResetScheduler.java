package com.lauram.aiproxy.scheduler;

import com.lauram.aiproxy.domain.UserAccount;
import com.lauram.aiproxy.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks: resets the rate limit every minute and the monthly quota on the first day of the month.
 */
@Component
public class QuotaResetScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotaResetScheduler.class);

    private final UserAccountRepository accountRepository;

    public QuotaResetScheduler(UserAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    public void resetRateLimits() {
        accountRepository.findAll().forEach(UserAccount::resetRateWindow);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyQuotas() {
        accountRepository.findAll().forEach(UserAccount::resetMonthlyQuota);
        LOGGER.info("Monthly token quotas reset for {} users", accountRepository.findAll().size());
    }
}
