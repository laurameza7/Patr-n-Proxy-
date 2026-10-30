package com.lauram.aiproxy.controller;

import com.lauram.aiproxy.dto.DailyUsageResponse;
import com.lauram.aiproxy.dto.QuotaStatusResponse;
import com.lauram.aiproxy.dto.UpgradeRequest;
import com.lauram.aiproxy.dto.UpgradeResponse;
import com.lauram.aiproxy.service.QuotaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private static final String USER_HEADER = "X-User-Id";
    private static final String DEFAULT_USER = "demo-user";

    private final QuotaService quotaService;

    public QuotaController(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @GetMapping("/status")
    public QuotaStatusResponse status(@RequestHeader(value = USER_HEADER, defaultValue = DEFAULT_USER) String userId) {
        return quotaService.getStatus(userId);
    }

    @GetMapping("/history")
    public List<DailyUsageResponse> history(@RequestHeader(value = USER_HEADER, defaultValue = DEFAULT_USER) String userId) {
        return quotaService.getHistory(userId);
    }

    @PostMapping("/upgrade")
    public UpgradeResponse upgrade(@RequestHeader(value = USER_HEADER, defaultValue = DEFAULT_USER) String userId,
                                   @RequestBody(required = false) UpgradeRequest request) {
        UpgradeRequest safeRequest = request != null ? request : new UpgradeRequest(null, null, null);
        return quotaService.upgrade(userId, safeRequest);
    }
}
