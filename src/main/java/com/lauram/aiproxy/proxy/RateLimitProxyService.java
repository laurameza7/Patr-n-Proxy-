package com.lauram.aiproxy.proxy;

import com.lauram.aiproxy.domain.UserAccount;
import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.dto.GenerationResponse;
import com.lauram.aiproxy.exception.RateLimitExceededException;
import com.lauram.aiproxy.repository.UserAccountRepository;
import com.lauram.aiproxy.service.AIGenerationService;
import com.lauram.aiproxy.service.RateLimitWindow;

/**
 * Proxy #1 (protection proxy): verifies that the user does not exceed N requests per minute
 * according to the plan. If the limit is exceeded it stops the chain with a 429 error;
 * otherwise it delegates to the next element of the chain.
 */
public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final UserAccountRepository accountRepository;
    private final RateLimitWindow rateLimitWindow;

    public RateLimitProxyService(AIGenerationService next,
                                 UserAccountRepository accountRepository,
                                 RateLimitWindow rateLimitWindow) {
        this.next = next;
        this.accountRepository = accountRepository;
        this.rateLimitWindow = rateLimitWindow;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserAccount account = accountRepository.findOrCreate(request.getUserId());
        if (!account.tryAcquireRequestSlot()) {
            throw new RateLimitExceededException(
                    account.getPlan().getRequestsPerMinute(),
                    rateLimitWindow.secondsUntilReset());
        }
        return next.generate(request);
    }
}
