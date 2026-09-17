package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.config.properties.TelegramProperties;
import com.finance.dispatch.worker.dto.request.BotMessageRequest;
import com.finance.dispatch.worker.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/telegram/webhook")
public class TelegramAdminController {

    private final TelegramProperties telegramProperties;
    private final TelegramService telegramService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody BotMessageRequest botMessageRequest){
        telegramService.sendMessage(botMessageRequest.getText());
    }

    @GetMapping("/delete")
    public String deleteWebhook() {
        telegramService.deleteWebhook();
        return "✅ Webhook deleted & pending updates cleared!";
    }

    @GetMapping("/update")
    public String updateWebhook() {
        telegramService.updateWebhook();
        return "✅ Webhook deleted & pending updates cleared!";
    }

    @GetMapping("/set")
    public String resetWebhook() {
        telegramService.setWebhook();
        return "✅ Webhook has been set";
    }
}
