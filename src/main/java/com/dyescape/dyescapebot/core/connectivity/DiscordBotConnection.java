package com.dyescape.dyescapebot.core.connectivity;

import javax.inject.Singleton;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import com.dyescape.dyescapebot.exception.DyescapeBotConnectionException;

/**
 * Discord implementation of this bot.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Singleton
public class DiscordBotConnection implements BotConnection {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private JDA jda;

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void connect(String token, Handler<AsyncResult<Void>> handler) {
        try {
            this.jda = new JDABuilder(token)
                    .addEventListener((EventListener) event -> {
                        if (event instanceof ReadyEvent) {
                            handler.handle(Future.succeededFuture());
                        }
                    })
                    .build();
        } catch (Exception e) {
            handler.handle(Future.failedFuture(new DyescapeBotConnectionException(e)));
        }
    }
}
