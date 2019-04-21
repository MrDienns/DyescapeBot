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
import net.dv8tion.jda.core.entities.Member;

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
        this.moderation.kick(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason);
    }

    @Subcommand("ban")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently ban a user from the server")
    public void onBanCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.ban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason);
    }

    @Subcommand("tempban")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily ban a user from the server")
    public void onTempBanCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempban(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time));
    }

    @Subcommand("mute")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Permanently mute a user on the server")
    public void onMuteCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.mute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason);
    }

    @Subcommand("tempmute")
    @CommandPermission("moderator")
    @Syntax("<User> <Time> [Reason]")
    @Description("Temporarily mute a user on the server")
    public void onTempMuteCommand(JDACommandEvent e, Member member, String time, @Optional String reason) {
        this.moderation.tempmute(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason,
                TimeUtil.parseFromRelativeString(time));
    }

    @Subcommand("warn")
    @CommandPermission("moderator")
    @Syntax("<User> [Reason]")
    @Description("Warn a user on the server")
    public void onWarnCommand(JDACommandEvent e, Member member, @Optional String reason) {
        this.moderation.warn(member.getGuild().getIdLong(), member.getUser().getIdLong(), reason);
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
}
