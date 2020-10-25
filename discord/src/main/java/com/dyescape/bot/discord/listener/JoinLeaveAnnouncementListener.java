package com.dyescape.bot.discord.listener;

import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.util.DiscordMessage;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinLeaveAnnouncementListener extends ListenerAdapter {

    private final DataSuit dataSuit;

    public JoinLeaveAnnouncementListener(DataSuit dataSuit) {
        this.dataSuit = dataSuit;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(event.getGuild().getId());
        String channel = serverEntity.getJoinLeaveChannel();
        if (channel == null || channel.isEmpty()) return;
        String joinMessage = serverEntity.getJoinMessage();
        if (joinMessage == null || joinMessage.isEmpty()) return;
        TextChannel textChannel = event.getGuild().getTextChannelById(channel);
        if (textChannel == null) return;
        Message message = new MessageBuilder()
                .append(event.getUser().getAsMention())
                .setEmbed(buildJoinMessageEmbed(event.getGuild(), event.getUser(), joinMessage))
                .build();
        textChannel.sendMessage(message).submit();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(event.getGuild().getId());
        String channel = serverEntity.getJoinLeaveChannel();
        if (channel == null || channel.isEmpty()) return;
        String leaveMessage = serverEntity.getLeaveMessage();
        if (leaveMessage == null || leaveMessage.isEmpty()) return;
        TextChannel textChannel = event.getGuild().getTextChannelById(channel);
        if (textChannel == null) return;
        Message message = new MessageBuilder()
                .append(event.getUser().getAsMention())
                .setEmbed(buildLeaveMessageEmbed(event.getUser(), leaveMessage))
                .build();
        textChannel.sendMessage(message).submit();
    }

    private MessageEmbed buildJoinMessageEmbed(Guild guild, User user, String message) {
        message = message.replace("{user_name}", user.getName());
        message = message.replace("{user_discriminator}", user.getDiscriminator());

        return DiscordMessage.CreateEmbeddedMessage(String.format("Welcome to %s, %s#%s!",
                guild.getName(), user.getName(), user.getDiscriminator()),
                message, user);
    }

    private MessageEmbed buildLeaveMessageEmbed(User user, String message) {
        message = message.replace("{user_name}", user.getName());
        message = message.replace("{user_discriminator}", user.getDiscriminator());

        return DiscordMessage.CreateEmbeddedMessage(null, message, user);
    }
}
