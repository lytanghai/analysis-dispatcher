package com.finance.dispatch.worker.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramProperties {

    private String appWebhookUrl;

    private String telegramUrl;

    private Boolean enabled;

    private String token;

    private String username;

    private String chatId;

    private String deleteWebhook;

    private String updateWebhook;

    private String setWebhook;

}
