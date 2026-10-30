package com.lauram.aiproxy.controller;

import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.dto.GenerationResponse;
import com.lauram.aiproxy.service.AIGenerationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Client of the Proxy pattern: it depends only on AIGenerationService
 * and is unaware of the proxy chain behind it.
 */
@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiGenerationService;

    public AIController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
                                       @Valid @RequestBody GenerationRequest request) {
        request.setUserId(userId);
        return aiGenerationService.generate(request);
    }
}
