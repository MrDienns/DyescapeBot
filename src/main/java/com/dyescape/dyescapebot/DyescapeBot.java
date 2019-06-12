package com.dyescape.dyescapebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class of the bot. Initializes Spring.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableAutoConfiguration
public class DyescapeBot {

    /**
     * Main static bootstrap method to initialize Spring.
     * @param args
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public static void main(String[] args) {
        SpringApplication.run(DyescapeBot.class, args);
    }
}
