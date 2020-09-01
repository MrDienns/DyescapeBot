package com.dyescape.bot.discord.command.general;

import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.BotCommand;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.Punishment;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class InfoCommand extends BotCommand {

    public InfoCommand(JDA jda, DataSuit dataSuit) {
        super(jda, dataSuit);
    }

    @CommandAlias("info")
    @Syntax("[user]")
    @Description("Show general user & moderation info")
    public void info(JDACommandEvent e, @Optional User user) {

        User targetUser = user == null ? this.getUserFromJDA(e.getIssuer().getAuthor()) : user;
        if (!(targetUser instanceof DiscordUser)) {
            throw new IllegalStateException("User is not an instance of a DiscordUser?");
        }
        net.dv8tion.jda.api.entities.User targetJdaUser = ((DiscordUser) targetUser).getJdaUser();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(this.getDiscordUserName(targetJdaUser), null, targetJdaUser.getAvatarUrl());
        builder.setDescription(this.getUserInfoAsString(this.getServerFromJDA(e.getIssuer().getGuild()), (DiscordUser) targetUser));

        e.getIssuer().getChannel().sendMessage(builder.build()).queue();
    }

    private String getUserInfoAsString(Server server, DiscordUser discordUser) {
        StringBuilder builder = new StringBuilder("\n**User information**\n");

        builder.append(String.format("⠀ID: `%s`\n", discordUser.getJdaUser().getId()));
        builder.append(String.format("⠀Created: `%s`\n", this.formatTime(discordUser.getJdaUser().getTimeCreated())));

        builder.append("\n**Active punishments & warning points**\n");
        builder.append(String.format("⠀Warning points: %s\n", discordUser.getActiveWarningPoints(server)));
        List<Punishment> active = discordUser.getActivePunishment(server);
        for (Punishment punishment : active) {
            if (punishment.getAction().equalsIgnoreCase("WARN")) continue;
            if (punishment.getExpiresAt() != null) {
                builder.append(String.format("⠀%s, expires on %s.\n", punishment.getAction(),
                        this.formatTime(punishment.getExpiresAt().atOffset(ZoneOffset.UTC))));
            } else {
                builder.append(String.format("⠀Permanent %s.\n", punishment.getAction()));
            }
        }

        return builder.toString();
    }

    private String formatTime(OffsetDateTime time) {
        return String.format("%s %s %s, %s:%s:%s (UTC)", time.getDayOfMonth(), this.upperCaseFirst(time.getMonth().name()),
                time.getYear(), time.getHour(), time.getMinute(), time.getSecond());
    }

    private String upperCaseFirst(String text) {
        text = text.toLowerCase();
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String getDiscordUserName(net.dv8tion.jda.api.entities.User user) {
        return String.format("%s#%s", user.getName(), user.getDiscriminator());
    }
}
