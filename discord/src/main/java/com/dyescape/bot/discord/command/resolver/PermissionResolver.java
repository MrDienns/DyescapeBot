package com.dyescape.bot.discord.command.resolver;

import co.aikar.commands.CommandPermissionResolver;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.JDACommandManager;
import net.dv8tion.jda.api.entities.Role;

import java.util.Objects;

public class PermissionResolver implements CommandPermissionResolver {

    @Override
    public boolean hasPermission(JDACommandManager manager, JDACommandEvent event, String permission) {
        // TODO: Entity based roles and permissions
        return Objects.requireNonNull(event.getIssuer().getMember()).getRoles().stream()
                .map(Role::getName).anyMatch(r -> r.equalsIgnoreCase("moderator"));
    }
}
