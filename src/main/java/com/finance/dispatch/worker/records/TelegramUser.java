package com.finance.dispatch.worker.records;

public record TelegramUser(
        Long id,
        Boolean is_bot,
        String first_name,
        String last_name,
        String username
) {
}