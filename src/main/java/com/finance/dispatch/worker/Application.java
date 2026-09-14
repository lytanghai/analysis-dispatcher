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
 * Test
 * Apply Telegram
 *jdbc:postgresql://aws-0-ap-southeast-2.pooler.supabase.com:5432/postgres
 * postgres.bavvydtynqhlaiijignh
 *
 * */