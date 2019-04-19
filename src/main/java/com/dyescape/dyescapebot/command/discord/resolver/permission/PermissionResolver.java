package com.dyescape.dyescapebot.command.discord.resolver.permission;

import co.aikar.commands.CommandPermissionResolver;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.JDACommandManager;

public class PermissionResolver implements CommandPermissionResolver {

    @Override
    public boolean hasPermission(JDACommandManager manager, JDACommandEvent event, String permission) {

        // TODO: Load user groups
        //  > Check if one of those groups is marked as moderator

        return event.getIssuer().getMember().getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(permission));
    }
}
