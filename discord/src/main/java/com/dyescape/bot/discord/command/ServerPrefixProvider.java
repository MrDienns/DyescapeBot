package com.dyescape.bot.discord.command;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.repository.ServerRepository;

import java.util.Optional;

public class ServerPrefixProvider {

    public static final String DEFAULT_COMMAND_PREFIX = "!";

    private final ServerRepository serverRepository;

    public ServerPrefixProvider(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public String getPrefix(String guild) {
        return this.getServerEntityFromGuildID(guild).getCommandPrefix();
    }

    private ServerEntity getServerEntityFromGuildID(String guildId) {
        Optional<ServerEntity> result = this.serverRepository.findById(guildId);
        // Should always be true under normal conditions
        return result.orElseGet(() -> new ServerEntity(guildId, DEFAULT_COMMAND_PREFIX));
    }
}
