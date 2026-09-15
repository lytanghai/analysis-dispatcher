package com.finance.dispatch.worker.service;

import com.finance.dispatch.worker.config.properties.TelegramProperties;
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

    public void sendMessage(BotMessageRequest botMessageRequest) {
        log.info("Request Received!");
        this.post("/sendMessage", botMessageRequest);
    }

    private String processCommand(String chatId, String command) throws Exception {

        log.info("incoming command {}", command);

        if (command.trim().startsWith("*monthly:")) {
            return "";
        }

        if(command.startsWith("*asset:")) {
            if (!"678134373".equals(chatId)) {
                return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
            } else {
                return "";
            }
        }

        switch (command) {
            case "/llist":
                return "";

            case "/assets":
                if (!"678134373".equals(chatId)) {
                    return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
                } else {
                    return "";
                }
            //-----------------------------------------------------------------

            case "/help":
                return
                    "*🤖 Ｂｏｔ Ｃｏｍｍａｎｄｓ Ｈｅｌｐ*\n\n" +
                    "📅 /calendar \\- Show this week's important events (US)\n" +
                    "💰 /gold \\- Show the real\\-time live price of gold\n" +
                    "🔔 /subscribe \\- Receive alerts and important announcements\n" +
                    "❌ /unsubscribe \\- Stop receiving alerts and announcements\n" +
                    
                    "📊 /budget \\- Check monthly budget breakdown\n" +
                    "⭐ /clsbud \\- Clear monthly budget\n" +
                    
                    "🔁 /llist \\- List recurring alerts\n" +
                    "⏰ /loop \\- Manage looping reminders\n" +
                        "• *Add:* `/loop +10m drink water`\n" +
                        "• *Remove:* `/loop - 1`\n" +
                        "• *Clear:* `/loop *`\n" +
                    "⭐ /assettemplate \\-Get Asset Register Template\n" +
                    "💡 *Tip:* _Use the commands exactly as shown above._\n\n";

            default:
                return "";
        }
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
                "default-connector",
                endpoint,
                body,
                Void.class
        );
    }

    private void get(String endpoint) {
        restClientHttpUtils.get(
                "default-connector",
                endpoint,
                Void.class
        );
    }

}
