package com.lauram.aiproxy.dto;

import com.lauram.aiproxy.domain.PlanType;

/**
 * Upgrade request with the simulated payment data. Defaults to PRO.
 */
public record UpgradeRequest(PlanType targetPlan, String cardHolder, String cardLastDigits) {

    public PlanType resolvedTargetPlan() {
        return targetPlan == null ? PlanType.PRO : targetPlan;
    }
}
