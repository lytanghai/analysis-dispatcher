package com.finance.dispatch.worker.service;


import com.finance.dispatch.worker.config.component.TelegramOnSentComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramService {

    private final TelegramOnSentComponent telegramOnSentComponent;


    public void handleUpdate(Update update) throws Exception {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String reply = processCommand(
                    chatId,
                    update.getMessage().getText()
            );

            telegramOnSentComponent.send(chatId, reply);
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
