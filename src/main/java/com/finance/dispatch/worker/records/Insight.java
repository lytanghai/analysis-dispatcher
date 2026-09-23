package com.finance.dispatch.worker.records;

import java.math.BigDecimal;

public record Insight(
        String date,
        BigDecimal highest,
        BigDecimal lowest,
        BigDecimal changed,
        String event
) {
}