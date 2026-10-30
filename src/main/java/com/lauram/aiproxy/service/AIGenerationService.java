package com.lauram.aiproxy.service;

import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.dto.GenerationResponse;

/**
 * Subject of the Proxy pattern: common interface shared by the real service and every proxy,
 * so the client never knows whether it is talking to a proxy or to the real object.
 */
public interface AIGenerationService {

    GenerationResponse generate(GenerationRequest request);
}
