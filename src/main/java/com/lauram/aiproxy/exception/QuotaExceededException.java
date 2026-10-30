package com.lauram.aiproxy.exception;

/**
 * Raised by QuotaProxyService when the monthly token quota is exhausted.
 * Mapped to HTTP 402 Payment Required.
 */
public class QuotaExceededException extends RuntimeException {

    public QuotaExceededException(long requiredTokens, long remainingTokens) {
        super("Cuota mensual agotada: la solicitud requiere ~" + requiredTokens
                + " tokens y solo quedan " + remainingTokens + ". Mejora tu plan para continuar.");
    }
}
