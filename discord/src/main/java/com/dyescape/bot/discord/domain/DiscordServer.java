package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.domain.model.Server;

import net.dv8tion.jda.api.entities.Guild;

public class DiscordServer implements Server {

    private final ServerEntity serverEntity;
    private final Guild server;

    public DiscordServer(ServerEntity serverEntity, Guild server) {
        this.serverEntity = serverEntity;
        this.server = server;
    }

    @Override
    public String getId() {
        return this.server.getId();
    }
}
