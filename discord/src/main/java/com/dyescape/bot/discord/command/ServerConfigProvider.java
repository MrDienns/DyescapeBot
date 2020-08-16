package com.dyescape.bot.discord.command;

import co.aikar.commands.CommandConfig;
import co.aikar.commands.CommandConfigProvider;
import net.dv8tion.jda.api.entities.ChannelType;
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

        if (!event.isFromType(ChannelType.TEXT)) return null;

        List<String> prefixes = new ArrayList<>();
        String prefix = this.serverPrefixProvider.getPrefix(event.getGuild().getId());
        prefixes.add(prefix);

        return () -> prefixes;
    }
}
