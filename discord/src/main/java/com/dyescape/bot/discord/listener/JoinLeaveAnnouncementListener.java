package com.dyescape.bot.discord.listener;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.util.DiscordMessage;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
        textChannel.sendMessage(buildMessage(event.getGuild(), event.getUser(), joinMessage)).submit();
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
        textChannel.sendMessage(buildMessage(event.getGuild(), event.getUser(), leaveMessage)).submit();
    }

    private MessageEmbed buildMessage(Guild guild, User user, String message) {
        message = message.replace("{user_name}", user.getName());
        message = message.replace("{user_discriminator}", user.getDiscriminator());

        return DiscordMessage.CreateEmbeddedMessage(String.format("Welcome to %s, %s#%s!",
                guild.getName(), user.getName(), user.getDiscriminator()),
                message, user);
    }
}
