package com.finance.dispatch.worker.records;

public record TelegramMessage(
        Long message_id,
        TelegramChat chat,
        TelegramUser from,
        String text
) {
}