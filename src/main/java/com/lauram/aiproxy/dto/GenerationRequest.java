package com.lauram.aiproxy.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Prompt sent by a user to the AI service.
 * The userId is filled by the controller from the X-User-Id header.
 */
public class GenerationRequest {

    private String userId;

    @NotBlank(message = "El prompt no puede estar vacío")
    @Size(max = 4000, message = "El prompt no puede superar 4000 caracteres")
    private String prompt;

    @Min(value = 64, message = "maxTokens mínimo es 64")
    @Max(value = 4096, message = "maxTokens máximo es 4096")
    private int maxTokens = 512;

    public GenerationRequest() {
    }

    public GenerationRequest(String userId, String prompt, int maxTokens) {
        this.userId = userId;
        this.prompt = prompt;
        this.maxTokens = maxTokens;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
}
