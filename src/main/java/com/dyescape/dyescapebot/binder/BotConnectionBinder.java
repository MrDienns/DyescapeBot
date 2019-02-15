package com.dyescape.dyescapebot.binder;

import com.google.inject.AbstractModule;

import com.dyescape.dyescapebot.core.connectivity.BotConnection;
import com.dyescape.dyescapebot.core.connectivity.DiscordBotConnection;

/**
 * InjectorProviderBinder is an extension of Guice's AbstractModule which
 * is used to bind the {@link BotConnection} interface to the
 * implementation of {@link DiscordBotConnection} when injecting the
 * {@link BotConnection} interface.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class BotConnectionBinder extends AbstractModule {

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    protected void configure() {
        this.bind(BotConnection.class).to(DiscordBotConnection.class);
    }
}
