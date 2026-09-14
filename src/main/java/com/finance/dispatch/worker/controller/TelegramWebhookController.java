package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.service.TelegramService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final TelegramService telegramBotService;

    public TelegramWebhookController(TelegramService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public void onUpdateReceived(@RequestBody Update update) throws Exception {
        telegramBotService.handleUpdate(update);
    }
}
