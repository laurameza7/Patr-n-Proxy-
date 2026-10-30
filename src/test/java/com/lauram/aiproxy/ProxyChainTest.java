package com.lauram.aiproxy;

import com.lauram.aiproxy.domain.PlanType;
import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.exception.QuotaExceededException;
import com.lauram.aiproxy.exception.RateLimitExceededException;
import com.lauram.aiproxy.proxy.QuotaProxyService;
import com.lauram.aiproxy.proxy.RateLimitProxyService;
import com.lauram.aiproxy.repository.UserAccountRepository;
import com.lauram.aiproxy.service.AIGenerationService;
import com.lauram.aiproxy.service.MockAIGenerationService;
import com.lauram.aiproxy.service.QuotaService;
import com.lauram.aiproxy.service.RateLimitWindow;
import com.lauram.aiproxy.service.TokenEstimator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProxyChainTest {

    private UserAccountRepository repository;
    private AIGenerationService chain;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.systemDefaultZone();
        repository = new UserAccountRepository();
        TokenEstimator estimator = new TokenEstimator();
        RateLimitWindow window = new RateLimitWindow(clock);
        QuotaService quotaService = new QuotaService(repository, window, clock);
        AIGenerationService real = new MockAIGenerationService(0, estimator);
        AIGenerationService quota = new QuotaProxyService(real, repository, estimator, quotaService, clock);
        chain = new RateLimitProxyService(quota, repository, window);
    }

    @Test
    void freePlanIsBlockedAfterTenRequestsPerMinute() {
        for (int i = 0; i < PlanType.FREE.getRequestsPerMinute(); i++) {
            chain.generate(new GenerationRequest("ana", "hola", 64));
        }
        assertThrows(RateLimitExceededException.class,
                () -> chain.generate(new GenerationRequest("ana", "hola", 64)));
    }

    @Test
    void tokensAreDeductedAfterEachRequest() {
        var response = chain.generate(new GenerationRequest("luis", "explica el patrón proxy", 256));
        assertTrue(response.totalTokens() > 0);
        assertEquals(response.totalTokens(), repository.findOrCreate("luis").getTokensUsedThisMonth());
    }

    @Test
    void exhaustedQuotaIsRejected() {
        repository.findOrCreate("sofi").consumeTokens(PlanType.FREE.getMonthlyTokens(), java.time.LocalDate.now());
        assertThrows(QuotaExceededException.class,
                () -> chain.generate(new GenerationRequest("sofi", "hola", 64)));
    }

    @Test
    void enterprisePlanIsUnlimited() {
        repository.findOrCreate("corp").changePlan(PlanType.ENTERPRISE);
        for (int i = 0; i < 80; i++) {
            chain.generate(new GenerationRequest("corp", "hola", 64));
        }
        assertEquals(80, repository.findOrCreate("corp").getRequestsInCurrentWindow());
    }
}
