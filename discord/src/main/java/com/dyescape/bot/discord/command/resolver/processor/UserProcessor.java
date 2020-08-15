package com.dyescape.bot.discord.command.resolver.processor;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserProcessor implements ArgumentProcessor<User> {

    private static final Pattern USER_ID = Pattern.compile("[<@!]*?\\d+[>]?");
    private static final Pattern USER_DISCRIMINATOR = Pattern.compile("([\\w]+)#(\\d{4})");

    private final JDA jda;

    public UserProcessor(JDA jda) {
        this.jda = jda;
    }

    @Override
    public User process(String argument) {

        Matcher idMatcher = USER_ID.matcher(argument);
        Matcher discriminatorMatcher = USER_DISCRIMINATOR.matcher(argument);

        if (idMatcher.matches()) {
            argument = argument
                    .replace("<", "")
                    .replace("@", "")
                    .replace("!", "")
                    .replace(">", "");

            User result = this.jda.getUserById(argument);
            if (result == null) {
                result = this.jda.retrieveUserById(argument, false).complete();
            }

            return result;
        } else if (discriminatorMatcher.matches()) {

            String name = discriminatorMatcher.group(1);
            String discriminator = discriminatorMatcher.group(2);

            return this.jda.getUserByTag(name, discriminator);
        } else {
            return null;
        }
    }
}
