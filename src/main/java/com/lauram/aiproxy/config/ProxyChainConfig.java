package com.lauram.aiproxy.config;

import com.lauram.aiproxy.proxy.QuotaProxyService;
import com.lauram.aiproxy.proxy.RateLimitProxyService;
import com.lauram.aiproxy.repository.UserAccountRepository;
import com.lauram.aiproxy.service.AIGenerationService;
import com.lauram.aiproxy.service.MockAIGenerationService;
import com.lauram.aiproxy.service.QuotaService;
import com.lauram.aiproxy.service.RateLimitWindow;
import com.lauram.aiproxy.service.TokenEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Assembles the proxy chain: Client -> RateLimitProxy -> QuotaProxy -> MockAIGenerationService.
 * The controller only receives the AIGenerationService interface.
 */
@Configuration
public class ProxyChainConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Bogota"));
    }

    @Bean
    public AIGenerationService aiGenerationService(UserAccountRepository accountRepository,
                                                   TokenEstimator tokenEstimator,
                                                   QuotaService quotaService,
                                                   RateLimitWindow rateLimitWindow,
                                                   Clock clock,
                                                   @Value("${app.mock.latency-ms:1200}") long latencyMs) {
        AIGenerationService realService = new MockAIGenerationService(latencyMs, tokenEstimator);
        AIGenerationService quotaProxy = new QuotaProxyService(
                realService, accountRepository, tokenEstimator, quotaService, clock);
        return new RateLimitProxyService(quotaProxy, accountRepository, rateLimitWindow);
    }
}
