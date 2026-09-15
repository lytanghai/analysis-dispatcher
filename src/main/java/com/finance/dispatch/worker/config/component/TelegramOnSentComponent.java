package com.finance.dispatch.worker.config.component;

import com.finance.dispatch.worker.service.TelegramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class TelegramOnSentComponent {

    private final TelegramService telegramService;

    public TelegramOnSentComponent(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public void send(String chatId, String text) {
        telegramService.sendMessage(Long.valueOf(chatId), text);
    }
}
