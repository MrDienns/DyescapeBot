package com.dyescape.dyescapebot.core.connectivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import co.aikar.commands.JDACommandContexts;
import co.aikar.commands.JDACommandManager;
import com.google.inject.Injector;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import com.dyescape.dyescapebot.command.discord.GeneralHelpCommand;
import com.dyescape.dyescapebot.command.discord.ModerationCommand;
import com.dyescape.dyescapebot.command.discord.resolver.MemberResolver;
import com.dyescape.dyescapebot.command.discord.resolver.permission.PermissionResolver;
import com.dyescape.dyescapebot.exception.DyescapeBotConnectionException;
import com.dyescape.dyescapebot.provider.InjectorProvider;

/**
 * Discord implementation of this bot.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Singleton
public class DiscordBotConnection implements BotConnection {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private InjectorProvider injectorProvider;

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private JDA jda;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public DiscordBotConnection(InjectorProvider injectorProvider) {
        this.injectorProvider = injectorProvider;
    }

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

            this.jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT, "Dyescape"));

            Injector injector = this.injectorProvider.getInjector();

            JDACommandManager commandManager = new JDACommandManager(this.jda);

            JDACommandContexts contexts = (JDACommandContexts) commandManager.getCommandContexts();
            contexts.registerContext(Member.class, new MemberResolver());

            commandManager.enableUnstableAPI("help");
            commandManager.setPermissionResolver(new PermissionResolver());

            commandManager.registerCommand(injector.getInstance(ModerationCommand.class));
            commandManager.registerCommand(injector.getInstance(GeneralHelpCommand.class));
        } catch (Exception e) {
            handler.handle(Future.failedFuture(new DyescapeBotConnectionException(e)));
        }
    }
}
