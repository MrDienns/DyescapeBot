package com.dyescape.bot.discord.command;

import co.aikar.commands.CommandConfig;
import co.aikar.commands.CommandConfigProvider;
import com.google.common.base.Strings;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerConfigProvider implements CommandConfigProvider {

    private final ServerPrefixProvider serverPrefixProvider;

    public ServerConfigProvider(ServerPrefixProvider serverPrefixProvider) {
        this.serverPrefixProvider = serverPrefixProvider;
    }

    @Override
    public CommandConfig provide(MessageReceivedEvent event) {

        List<String> prefixes = new ArrayList<>();
        String prefix = this.serverPrefixProvider.getPrefix(event.getGuild().getId());

        // Add excessive space margins for people who can't type, but ACF doesn't seem to do this
        // TODO: Make a PR to ACF to fix this
        for (int i = 5; i >= 0; i--) {
            prefixes.add(prefix + Strings.repeat(" ", i));
        }

        return () -> prefixes;
    }
}
