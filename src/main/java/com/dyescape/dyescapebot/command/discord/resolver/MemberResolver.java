package com.dyescape.dyescapebot.command.discord.resolver;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.JDACommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.dv8tion.jda.core.entities.Member;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolver class used by ACF (Annotation Command Framework) to resolve the {@link Member} parameter of created
 * commands. The resolver forces users to explicitly tag the user.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
public class MemberResolver implements ContextResolver<Member, JDACommandExecutionContext> {

    private static final Pattern MEMBER_PATTERN = Pattern.compile("<@.*?(\\d+)>");

    @Override
    public Member getContext(JDACommandExecutionContext context) throws InvalidCommandArgument {

        // popFirstArgument will always give us the correct argument string
        String argument = context.popFirstArg();

        // Let's get the Discord ID from the tag string
        Matcher idMatcher = MEMBER_PATTERN.matcher(argument);
        if (!idMatcher.find()) {
            throw new InvalidCommandArgument("Please tag the user.");
        }

        // Let's load the member
        String memberId = idMatcher.group(1);
        Member member = context.getIssuer().getEvent().getGuild().getMemberById(memberId);

        // Just a double check, this should never occur...
        if (member == null) {
            throw new InvalidCommandArgument("Specified member could not be found.");
        }

        // We got the member, so let's return it
        return member;
    }
}
