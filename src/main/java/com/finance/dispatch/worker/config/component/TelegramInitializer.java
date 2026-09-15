package com.finance.dispatch.worker.config.component;

import com.finance.dispatch.worker.config.properties.TelegramProperties;
import com.finance.dispatch.worker.service.TelegramService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramInitializer {

    private final TelegramService telegramService;
    private final TelegramProperties properties;

    @PostConstruct
    public void initialize() throws InterruptedException {

        if (!properties.getEnabled()) {
            return;
        }

        log.info("Initializing Telegram...");

        telegramService.deleteWebhook();
        Thread.sleep(2000);
        telegramService.updateWebhook();
        Thread.sleep(2000);
        telegramService.setWebhook();

        log.info("Telegram initialization completed");
    }
}