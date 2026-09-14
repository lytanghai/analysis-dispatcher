package com.finance.dispatch.worker.config.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramOnReceivedComponent extends TelegramLongPollingBot {

    // Return the bot token you got from @BotFather

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUserName;

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    // Return the bot username you chose
    @Override
    public String getBotUsername() {
        return this.botUserName;
    }

    // This method is called whenever your bot receives an update (new message, etc.)
    @Override
    public void onUpdateReceived(Update update) {
        // Check if the update contains a message with text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Example: Echo back the received message
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("You said: " + messageText);
            System.out.println("I SAID " + messageText);

            try {
                execute(message); // Send the message back to the chat
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}