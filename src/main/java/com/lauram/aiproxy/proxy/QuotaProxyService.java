package com.lauram.aiproxy.proxy;

import com.lauram.aiproxy.domain.UserAccount;
import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.dto.GenerationResponse;
import com.lauram.aiproxy.exception.QuotaExceededException;
import com.lauram.aiproxy.repository.UserAccountRepository;
import com.lauram.aiproxy.service.AIGenerationService;
import com.lauram.aiproxy.service.QuotaService;
import com.lauram.aiproxy.service.TokenEstimator;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Proxy #2 (protection + accounting proxy): verifies that the user still has monthly tokens,
 * delegates to the real service and then deducts the tokens actually consumed.
 * If the quota is exhausted it stops the chain with a 402 error.
 */
public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService realService;
    private final UserAccountRepository accountRepository;
    private final TokenEstimator tokenEstimator;
    private final QuotaService quotaService;
    private final Clock clock;

    public QuotaProxyService(AIGenerationService realService,
                             UserAccountRepository accountRepository,
                             TokenEstimator tokenEstimator,
                             QuotaService quotaService,
                             Clock clock) {
        this.realService = realService;
        this.accountRepository = accountRepository;
        this.tokenEstimator = tokenEstimator;
        this.quotaService = quotaService;
        this.clock = clock;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = accountRepository.findOrCreate(request.getUserId());
        long estimatedCost = tokenEstimator.estimateRequestCost(request.getPrompt(), request.getMaxTokens());
        if (!account.hasTokensFor(estimatedCost)) {
            throw new QuotaExceededException(estimatedCost, account.getRemainingTokens());
        }
        GenerationResponse response = realService.generate(request);
        account.consumeTokens(response.totalTokens(), LocalDate.now(clock));
        return response.withQuota(quotaService.getStatus(request.getUserId()));
    }
}
