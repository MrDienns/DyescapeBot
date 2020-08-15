package com.dyescape.bot.discord.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class DiscordMessage {

    public static MessageEmbed CreateEmbeddedMessage(String title, String body, User toUser) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(body);
        if (toUser != null) {
            builder.setAuthor(toUser.getAsTag(), null, toUser.getAvatarUrl());
        }
        return builder.build();
    }
}
