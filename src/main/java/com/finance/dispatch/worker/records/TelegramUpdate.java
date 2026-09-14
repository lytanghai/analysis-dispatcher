package com.finance.dispatch.worker.records;

public record TelegramUpdate(
        Long update_id,
        TelegramMessage message
) {
}