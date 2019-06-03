package com.dyescape.dyescapebot.command.discord.resolver;

import co.aikar.commands.CommandPermissionResolver;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.JDACommandManager;
import net.dv8tion.jda.core.Permission;

/**
 * Resolver class for ACF (Annotation Command Framework) used to resolve the permissions associated with created
 * commands. The mechanism will check if the user explicitly has the specific permission by directly checking if
 * the permission was given to the user.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class PermissionResolver implements CommandPermissionResolver {

    @Override
    public boolean hasPermission(JDACommandManager manager, JDACommandEvent event, String permissionString) {
        Permission permission = Permission.valueOf(permissionString);
        return event.getIssuer().getMember().hasPermission(permission);
    }
}
