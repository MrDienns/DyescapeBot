package com.dyescape.bot.discord.command.configuration;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.repository.ServerRepository;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

import java.util.Optional;

@CommandAlias("configuration")
public class ConfigurationCommand extends BaseCommand {

    private final ServerRepository serverRepository;

    public ConfigurationCommand(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Subcommand("setprefix")
    public void setPrefix(JDACommandEvent e, String prefix) {
        Optional<ServerEntity> query = this.serverRepository.findById(e.getEvent().getGuild().getId());
        ServerEntity serverEntity;
        if (query.isEmpty()) {
            serverEntity = new ServerEntity(e.getEvent().getGuild().getId(), prefix);
        } else {
            serverEntity = query.get();
        }
        serverEntity.setCommandPrefix(prefix);
        this.serverRepository.save(serverEntity);
        e.getEvent().getChannel().sendMessage(String.format("Changed command prefix to %s", prefix)).submit();
    }
}
