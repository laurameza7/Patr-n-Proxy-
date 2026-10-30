package com.lauram.aiproxy.exception;

/**
 * Raised by RateLimitProxyService when a user exceeds the requests allowed per minute.
 * Mapped to HTTP 429 Too Many Requests with a Retry-After header.
 */
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(int limitPerMinute, long retryAfterSeconds) {
        super("Superaste el límite de " + limitPerMinute + " solicitudes por minuto. Intenta de nuevo en "
                + retryAfterSeconds + " segundos.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
