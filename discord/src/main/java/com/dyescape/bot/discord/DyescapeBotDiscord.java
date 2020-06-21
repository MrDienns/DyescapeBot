package com.dyescape.bot.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EntityScan(basePackages = "com.dyescape.bot.data.entity")
@SpringBootApplication(scanBasePackages = "com.dyescape.bot")
@EnableJpaRepositories(basePackages = "com.dyescape.bot.data.repositories")
public class DyescapeBotDiscord {

    public static void main(String[] args) {
        SpringApplication.run(DyescapeBotDiscord.class, args);
    }
}

