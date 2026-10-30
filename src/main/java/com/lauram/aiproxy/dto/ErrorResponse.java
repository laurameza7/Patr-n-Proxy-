package com.lauram.aiproxy.dto;

import java.time.Instant;

public record ErrorResponse(int status, String error, String message, Long retryAfterSeconds,
                            Instant timestamp) {
}
