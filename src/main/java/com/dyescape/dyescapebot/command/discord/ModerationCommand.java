package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.*;
import com.dyescape.dyescapebot.configuration.moderation.ModerationConfiguration;
import com.dyescape.dyescapebot.entity.discord.Warning;
import com.dyescape.dyescapebot.entity.discord.WarningAction;
import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.repository.ModerationWarningActionRepository;
import com.dyescape.dyescapebot.util.TimeUtil;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

@CommandAlias("!")
public class ModerationCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final Moderation moderation;
    private final ModerationWarningActionRepository warningActionRepository;
    private final ModerationConfiguration moderationConfiguration;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public ModerationCommand(Moderation moderation, ModerationWarningActionRepository warningActionRepository,
                             ModerationConfiguration moderationConfiguration) {
        this.moderation = moderation;
        this.warningActionRepository = warningActionRepository;
        this.moderationConfiguration = moderationConfiguration;
    }

    // -------------------------------------------- //
    // PUNISHMENT COMMANDS
    // -------------------------------------------- //

    @Subcommand("kick")
    @CommandPermission("KICK_MEMBERS")
    @Syntax("<User> [Reason]")
    @Description("Kick a user from the server")
    public void onKickCommand(JDACommandEvent e, Member member, @Optional String reason) {
        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.kick(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was kicked.", member.getEffectiveName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("ban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User> [Reason]")
    @Description("Permanently ban a user from the server")
    public void onBanCommand(JDACommandEvent e, Member member, @Optional String reason) {
        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was banned.", member.getEffectiveName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("tempban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily ban a user from the server")
    public void onTempBanCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.tempban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    TimeUtil.parseFromRelativeString(time), e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was banned for %s.",
                    member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime)), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("unban")
    @CommandPermission("BAN_MEMBERS")
    @Syntax("<User>")
    @Description("Unban a user from the server")
    public void onUnbanCommand(JDACommandEvent e, User user) {
        if (user.isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.unban(e.getIssuer().getGuild().getIdLong(), user.getIdLong());
            e.sendMessage(this.embed(String.format("User %s was unbanned.",
                    user.getName() + user.getDiscriminator()), Color.GREEN));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("mute")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> [Reason]")
    @Description("Permanently mute a user on the server")
    public void onMuteCommand(JDACommandEvent e, Member member, @Optional String reason) {
        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was muted.", member.getEffectiveName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("tempmute")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily mute a user on the server")
    public void onTempMuteCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.tempmute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    TimeUtil.parseFromRelativeString(time), e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was muted for %s.",
                    member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime)), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("unmute")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User>")
    @Description("Unmute a user on the server")
    public void onUnmuteCommand(JDACommandEvent e, User user) {
        if (user.isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.unmute(e.getIssuer().getGuild().getIdLong(), user.getIdLong());
            e.sendMessage(this.embed(String.format("User %s#%s was unmuted.",
                    user.getName(), user.getDiscriminator()), Color.GREEN));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("channelban|banchannel")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel> [reason]")
    @Description("Ban a user from a channel (revokes read & write access)")
    public void onChannelBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                    @Optional String reason) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.channelBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was banned from channel #%s.",
                    member.getEffectiveName(), channel.getName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("channeltempban|tempbanchannel|tempchannelban")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily ban a user from a channel (revokes read & write access)")
    public void onChannelTempBanCommand(JDACommandEvent e, Member member, TextChannel channel,
                                        String time, @Optional String reason) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.channelTempBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time),
                    e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was banned from channel #%s for %s.",
                    member.getEffectiveName(), channel.getName(),
                    TimeUtil.parsePunishmentTime(punishmentTime)), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("unchannelmute|channelunmute|unmutechannel")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel>")
    @Description("Revokes a channel mute")
    public void onUnchannelMuteCommand(JDACommandEvent e, Member member, TextChannel channel) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.unchannelMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong());
            e.sendMessage(this.embed(String.format("User %s was unmuted in channel #%s.",
                    member.getEffectiveName(), channel.getName()), Color.GREEN));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("channelmute|mutechannel")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel> [Reason]")
    @Description("Mute a user in a channel (revokes write access)")
    public void onChannelMuteCommand(JDACommandEvent e, Member member, TextChannel channel, @Optional String reason) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.channelMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, e.getIssuer().getAuthor().getIdLong());
            e.sendMessage(this.embed(String.format("User %s was muted in channel #%s.",
                    member.getEffectiveName(), channel.getName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("channeltempmute|tempmutechannel|tempchannelmute")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel> <Time> [Reason]")
    @Description("Temporarily mute a user in a channel (revokes  write access)")
    public void onChannelTempMuteCommand(JDACommandEvent e, Member member, TextChannel channel,
                                         String time, @Optional String reason) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.channelTempMute(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong(), reason, TimeUtil.parseFromRelativeString(time),
                    e.getIssuer().getAuthor().getIdLong());
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            e.sendMessage(this.embed(String.format("User %s was muted in channel #%s for %s.",
                    member.getEffectiveName(), channel.getName(),
                    TimeUtil.parsePunishmentTime(punishmentTime)), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("unchannelban|channelunban|unbanchannel")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Channel>")
    @Description("Revokes a channel mute")
    public void onUnchannelBanCommand(JDACommandEvent e, Member member, TextChannel channel) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.unchannelBan(member.getGuild().getIdLong(), member.getUser().getIdLong(),
                    channel.getIdLong());
            e.sendMessage(this.embed(String.format("User %s was unmuted in channel #%s.",
                    member.getEffectiveName(), channel.getName()), Color.GREEN));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("warn")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> <Reason> [Time]")
    @Description("Warn a user on the server")
    public void onWarnCommand(JDACommandEvent e, Member member, String reason) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            this.moderation.warn(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                    e.getIssuer().getAuthor().getIdLong(),
                    this.moderationConfiguration.getWarningperiod());
            e.sendMessage(this.embed(String.format("User %s was warned.", member.getEffectiveName()), Color.RED));
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("pardon")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User> [Warning ID]")
    @Description("Invokes all or specific warnings given to a user")
    public void onPardonCommand(JDACommandEvent e, Member member, @Optional Long id) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        try {
            if (id != null && id != 0) {
                this.moderation.pardon(member.getGuild().getIdLong(), member.getUser().getIdLong(), id, true);
                e.sendMessage(this.embed(String.format("Revoked all warnings of %s.", member.getEffectiveName()),
                        Color.GREEN));
            } else {
                this.moderation.pardon(member.getGuild().getIdLong(), member.getUser().getIdLong());
                e.sendMessage(this.embed(String.format("Revoked a warning of %s.", member.getEffectiveName()),
                        Color.GREEN));
            }
        } catch (Exception ex) {
            this.handleError(e, ex);
        }
    }

    @Subcommand("warnings|listwarnings")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<User>")
    @Description("Lists all of the active warnings of a user")
    public void onListWarningsCommand(JDACommandEvent e, Member member) {

        if (member.getUser().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        List<Warning> warnings = this.moderation.getWarnings(e.getIssuer().getMember().getGuild().getIdLong(),
                member.getUser().getIdLong());

        if (warnings.isEmpty()) {

            e.sendMessage(this.embed("User has no active warnings.", Color.LIGHT_GRAY));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder(String.format(
                "Active warnings of %s:\n\n", member.getEffectiveName()));

        warnings.forEach(warning -> {

            if (Strings.isNullOrEmpty(warning.getReason())) {
                stringBuilder.append(String.format("#%s - *No reason provided*\n", warning.getId()));
            } else {
                stringBuilder.append(String.format("#%s - %s\n", warning.getId(), warning.getReason()));
            }

            if (warning.getEnd() != null) {
                stringBuilder.append(String.format("Expires: %s\n", warning.getEnd().toString()));
            }

            stringBuilder.append("\n");
        });

        e.sendMessage(this.embed(stringBuilder.toString(), Color.LIGHT_GRAY));
    }

    // -------------------------------------------- //
    // CONFIGURATION COMMANDS
    // -------------------------------------------- //

    @Subcommand("warnaction|warningaction")
    @CommandPermission("MESSAGE_MANAGE")
    @Syntax("<WarningPoints> <Action> [Time]")
    @Description("Configure the moderation actions for reached warning points")
    public void onWarningActionCommand(JDACommandEvent e, Integer warningPoints, WarningAction.WarningActionType type, @Optional String time) {

        if (e.getIssuer().getAuthor().isBot()) {
            e.sendMessage(this.embed("Nice try.", Color.RED));
            return;
        }

        if (type.toString().contains("TEMP") && time == null) {
            throw new IllegalArgumentException("Temporarily commands require the time (duration) argument");
        }

        if (time == null) {
            this.warningActionRepository.save(new WarningAction(e.getIssuer().getGuild().getIdLong(), warningPoints, type, -1));
            e.sendMessage(this.embed(String.format("I will %s users that reach %s warning points.",
                    type.toString(), warningPoints), Color.LIGHT_GRAY));
        } else {
            long punishmentTime = TimeUtil.parseFromRelativeString(time);
            this.warningActionRepository.save(new WarningAction(e.getIssuer().getGuild().getIdLong(), warningPoints, type, punishmentTime));
            e.sendMessage(this.embed(String.format("I will %s users that reach %s warning points for %s.",
                    type.toString(), warningPoints, TimeUtil.parsePunishmentTime(punishmentTime)), Color.LIGHT_GRAY));
        }
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private void handleError(JDACommandEvent e, Exception ex) {
        e.sendMessage(this.embed(String.format("Error: %s", ex.getMessage()), Color.LIGHT_GRAY));
    }

    private MessageEmbed embed(String message, Color color) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(color);
        return eb.build();
    }
}
