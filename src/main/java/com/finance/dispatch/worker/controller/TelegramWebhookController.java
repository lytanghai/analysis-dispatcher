package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.records.TelegramMessage;
import com.finance.dispatch.worker.records.TelegramUpdate;
import com.finance.dispatch.worker.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final TelegramService telegramBotService;

    public TelegramWebhookController(TelegramService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> consumeMessage(
            @RequestBody TelegramUpdate update) {

        if (update.message() == null) {
            return ResponseEntity.ok().build();
        }

        TelegramMessage message = update.message();

        String text = message.text();
        Long chatId = message.chat().id();
        String username = message.from().username();

        System.out.println("Username: " + username);
        System.out.println("Chat ID: " + chatId);
        System.out.println("Message: " + text);

        // Your business logic
        handleMessage(chatId, text);

        return ResponseEntity.ok().build();
    }

    private void handleMessage(Long chatId, String text) {
        if ("YES".equalsIgnoreCase(text)) {
            System.out.println("User said YES");
        } else if ("NO".equalsIgnoreCase(text)) {
            System.out.println("User said NO");
        } else {
            System.out.println("Unknown message: " + text);
        }
    }
}
