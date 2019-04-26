package com.dyescape.dyescapebot.command.discord.resolver;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RoleResolver implements ContextResolver<Role, JDACommandExecutionContext> {

    @Override
    public Role getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // popFirstArgument will always give us the correct argument string
        String argument = context.popFirstArg();

        // Stream over all roles from the guild
        List<Role> roles = context.getIssuer().getEvent().getGuild().getRoles().stream()
                .filter(role -> role.getName().equalsIgnoreCase(argument))
                .collect(Collectors.toList());

        // No role found
        if (roles.isEmpty()) {

            throw new InvalidCommandArgument("No role with such name was found.");
        }

        // Too many roles found
        if (roles.size() > 1) {

            throw new InvalidCommandArgument("Too many roles found with that name.");
        }

        return roles.get(0);
    }
}
