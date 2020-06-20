package com.dyescape.bot.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class DyescapeBotDiscord {

    public static void main(String[] args) {
        SpringApplication.run(DyescapeBotDiscord.class, args);
    }
}
