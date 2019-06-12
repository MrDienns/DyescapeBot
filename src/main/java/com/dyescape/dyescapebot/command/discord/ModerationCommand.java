package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.*;
import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.util.TimeUtil;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

@CommandAlias("!")
public class ModerationCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final Moderation moderation;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public ModerationCommand(Moderation moderation) {
        this.moderation = moderation;
    }

    // -------------------------------------------- //
    // PUNISHMENT COMMANDS
    // -------------------------------------------- //

    @Subcommand("kick")
    @CommandPermission("KICK_MEMBERS")
    @Syntax("<User> [Reason]")
    @Description("Kick a user from the server")
    public void onKickCommand(JDACommandEvent e, Member member, @Optional String reason) {

        try {
            this.moderation.kick(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was kicked.", member.getEffectiveName())));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("ban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User> [Reason]")
    @Description("Permanently ban a user from the server")
    public void onBanCommand(JDACommandEvent e, Member member, @Optional String reason) {

        try {
            this.moderation.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was banned.", member.getEffectiveName())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("tempban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily ban a user from the server")
    public void onTempBanCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {

        try {
            this.moderation.tempban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    TimeUtil.parseFromRelativeString(time), e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was banned for %s.",
                    member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("unban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User>")
    @Description("Unban a user from the server")
    public void onUnbanCommand(JDACommandEvent e, User user) {

        try {
            this.moderation.unban(e.getIssuer().getGuild().getIdLong(), user.getIdLong(),
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was unbanned.",
                    user.getName() + user.getDiscriminator())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("mute")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> [Reason]")
    @Description("Permanently mute a user on the server")
    public void onMuteCommand(JDACommandEvent e, Member member, @Optional String reason) {

        try {
            this.moderation.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was muted.", member.getEffectiveName())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("tempmute")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily mute a user on the server")
    public void onTempMuteCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {

        try {
            this.moderation.tempmute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    TimeUtil.parseFromRelativeString(time), e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was muted for %s.",
                    member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("unmute")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User>")
    @Description("Unmute a user on the server")
    public void onUnmuteCommand(JDACommandEvent e, User user) {

        try {
            this.moderation.unmute(e.getIssuer().getGuild().getIdLong(), user.getIdLong(),
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s#%s was unmuted.",
                    user.getName(), user.getDiscriminator())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("channelban|banchannel")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> <Channel> [reason]")
    @Description("Ban a user from a channel (revokes read & write access)")
    public void onChannelBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                    @Optional String reason) {

        try {
            this.moderation.channelBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was banned from channel #%s.",
                    member.getEffectiveName(), channel.getName())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("channeltempban|tempbanchannel|tempchannelban")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily ban a user from a channel (revokes read & write access)")
    public void onChannelTempBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                        String time, @Optional String reason) {

        try {
            this.moderation.channelTempBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time),
                    e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was banned from channel #%s for %s.",
                    member.getEffectiveName(), channel.getName(),
                    TimeUtil.parsePunishmentTime(punishmentTime))));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("channelmute|mutechannel")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> <Channel> [Reason]")
    @Description("Mute a user in a channel (revokes write access)")
    public void onChannelMuteCommand(JDACommandEvent e, Member member, TextChannel channel, @Optional String reason) {

        try {
            this.moderation.channelMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was muted in channel #%s.",
                    member.getEffectiveName(), channel.getName())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("channeltempmute|tempmutechannel|tempchannelmute")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily mute a user in a channel (revokes  write access)")
    public void onChannelTempMuteCommand(JDACommandEvent e, Member member, TextChannel channel,
                                         String time, @Optional String reason) {

        try {
            this.moderation.channelTempMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time),
                    e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was muted in channel #%s for %s.",
                    member.getEffectiveName(), channel.getName(),
                    TimeUtil.parsePunishmentTime(punishmentTime))));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    @Subcommand("warn")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> [Reason]")
    @Description("Warn a user on the server")
    public void onWarnCommand(JDACommandEvent e, Member member, @Optional String reason) {

        try {
            this.moderation.warn(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was warned.", member.getEffectiveName())));
        } catch (Exception ex) {
            this.handleError(e ,ex);
        }
    }

    // -------------------------------------------- //
    // CONFIGURATION COMMANDS
    // -------------------------------------------- //

    @Subcommand("warnaction|warningaction")
    @CommandPermission("MANAGE_ROLES")
    @Syntax("<WarningPoints> <Action> [Time]")
    @Description("Configure the moderation actions for reached warning points")
    public void onWarningActionCommand(JDACommandEvent e, Integer warningPoints, String action, @Optional String time) {
        if (Strings.isNullOrEmpty(action)) {

            e.sendMessage(String.format("I am going to set the action of %s warning points to %s, action time: %s",
                    warningPoints, action, time));
        } else {
            e.sendMessage(String.format("I am going to delete the action of %s warning points.",
                    warningPoints));
        }
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private void handleError(JDACommandEvent e, Exception ex) {
        e.sendMessage(this.embed(String.format("Error: %s", ex.getMessage())));
    }

    private MessageEmbed embed(String message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(Color.RED);
        return eb.build();
    }
}
