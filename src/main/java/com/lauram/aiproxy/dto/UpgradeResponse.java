package com.lauram.aiproxy.dto;

public record UpgradeResponse(String message, String previousPlan, String transactionId,
                              QuotaStatusResponse quota) {
}
