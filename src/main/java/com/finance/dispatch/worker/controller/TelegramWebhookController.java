package com.finance.dispatch.worker.controller;

import com.finance.dispatch.worker.records.TelegramMessage;
import com.finance.dispatch.worker.records.TelegramUpdate;
import com.finance.dispatch.worker.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final TelegramService telegramBotService;

    public TelegramWebhookController(TelegramService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> consumeMessage(@RequestBody TelegramUpdate update) {

        if (update.message() == null) {
            return ResponseEntity.ok().build();
        }

        TelegramMessage message = update.message();

        String text = message.text();
        Long chatId = message.chat().id();
        String username = message.from().username();

        log.info("Username: {}",username);
        log.info("Chat ID: {}", chatId);
        log.info("Message: {}", text);

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
}
