package com.lauram.aiproxy.dto;

import java.time.LocalDate;

public record DailyUsageResponse(LocalDate date, long tokens, int requests) {
}
