package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.config.properties.TelegramProperties;
import com.finance.dispatch.worker.constant.TypeConstant;
import com.finance.dispatch.worker.dto.request.BotMessageRequest;
import com.finance.dispatch.worker.util.RestClientHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramProperties telegramProperties;
    private final RestClientHttpUtils restClientHttpUtils;

    public void sendMessage(String text) {
        log.info("Request Received!");
        BotMessageRequest botMessageRequest = new BotMessageRequest();
        botMessageRequest.setText(text);
        botMessageRequest.setChatId(Long.valueOf(telegramProperties.getChatId()));
        this.post(
                telegramProperties.getTelegramUrl() + telegramProperties.getToken() + telegramProperties.getSendMessage(),
                botMessageRequest
        );
    }

    public void deleteWebhook() {
        this.get(
            telegramProperties.getTelegramUrl() +
            telegramProperties.getToken() +
            telegramProperties.getDeleteWebhook()
        );
    }

    public void updateWebhook() {
        this.get(
                telegramProperties.getTelegramUrl() +
                telegramProperties.getToken() +
                telegramProperties.getUpdateWebhook()
        );
    }

    public void setWebhook() {
        this.get(
                telegramProperties.getTelegramUrl() +
                telegramProperties.getToken() +
                telegramProperties.getSetWebhook() +
                telegramProperties.getAppWebhookUrl()
        );
    }

    private void post(String endpoint, BotMessageRequest body) {
        restClientHttpUtils.post(
                TypeConstant.DEFAULT_REQUESTER,
                endpoint,
                body,
                Void.class
        );
    }

    private void get(String endpoint) {
        restClientHttpUtils.get(
                TypeConstant.DEFAULT_REQUESTER,
                endpoint,
                Void.class
        );
    }

}
