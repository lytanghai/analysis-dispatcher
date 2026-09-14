package com.finance.dispatch.worker.config.component;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class TelegramOnSentComponent {

    private final TelegramComponent telegramComponent;

    public TelegramOnSentComponent(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    public void send(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        telegramComponent.sendMessage(message);
    }
}
