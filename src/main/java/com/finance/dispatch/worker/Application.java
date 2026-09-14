package com.finance.dispatch.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

/**
 * server: thcrypto
 * cron: thcrypto
 * db: thdevops
 * Test
 * Apply Telegram
 *
 * Telegram url
 * https://api.telegram.org/bot6146637472:x/deleteWebhook
 * https://api.telegram.org/bot6146637472:x/getUpdates
 * https://api.telegram.org/bot6146637472:x/status
 * https://api.telegram.org/bot6146637472:x/setWebhook?url=https://analysis-dispatcher.onrender.com/telegram/webhook
 *
 * */