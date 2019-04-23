package com.dyescape.dyescapebot.command.discord;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.util.TimeUtil;

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
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Kick a user from the server")
    public void onKickCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.kick(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was kicked.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("ban")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently ban a user from the server")
    public void onBanCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was banned.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("tempban")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily ban a user from the server")
    public void onTempBanCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time), handler -> {

            if (handler.succeeded()) {
                long punishmentTime = TimeUtil.parseFromRelativeString(time);
                e.sendMessage(this.embed(String.format("User %s was banned for %s.",
                        member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("unban")
    @CommandPermission("moderator")
    @Syntax("<User>")
    @Description("Unban a user from the server")
    public void onUnbanCommand(JDACommandEvent e, User user) {
        this.moderation.unban(e.getIssuer().getGuild().getIdLong(), user.getIdLong(), handler -> {

            if (handler.succeeded()) {

                e.sendMessage(this.embed(String.format("User %s was unbanned.",
                        user.getName() + user.getDiscriminator())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("mute")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently mute a user on the server")
    public void onMuteCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was muted.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("tempmute")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily mute a user on the server")
    public void onTempMuteCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempmute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time), handler -> {

            if (handler.succeeded()) {

                long punishmentTime = TimeUtil.parseFromRelativeString(time);
                e.sendMessage(this.embed(String.format("User %s was muted for %s.",
                        member.getEffectiveName(), TimeUtil.parsePunishmentTime(punishmentTime))));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("unmute")
    @CommandPermission("moderator")
    @Syntax("<User>")
    @Description("Unmute a user on the server")
    public void onUnmuteCommand(JDACommandEvent e, User user) {
        this.moderation.unmute(e.getIssuer().getGuild().getIdLong(), user.getIdLong(), handler -> {

            if (handler.succeeded()) {

                e.sendMessage(this.embed(String.format("User %s#%s was unmuted.",
                        user.getName(), user.getDiscriminator())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    @Subcommand("warn")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Warn a user on the server")
    public void onWarnCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.warn(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason, handler -> {

            if (handler.succeeded()) {
                e.sendMessage(this.embed(String.format("User %s was warned.", member.getEffectiveName())));
            } else {
                e.sendMessage(this.embed(String.format("Error: %s", handler.cause().getMessage())));
            }
        });
    }

    // -------------------------------------------- //
    // CONFIGURATION COMMANDS
    // -------------------------------------------- //

    @Subcommand("warnaction|warningaction")
    @CommandPermission("moderator")
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

    private MessageEmbed embed(String message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(message);
        eb.setColor(Color.RED);
        return eb.build();
    }
}
