package com.dyescape.bot.discord.command.configuration;

import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.entity.data.WarningActionEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.BotCommand;
import com.dyescape.bot.discord.command.CommandPermissions;
import com.dyescape.bot.domain.model.TimeFrame;

import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

@CommandAlias("configuration")
public class ConfigurationCommand extends BotCommand {

    public ConfigurationCommand(JDA jda, DataSuit dataSuit) {
        super(jda, dataSuit);
    }

    @Subcommand("setprefix")
    @CommandPermission(CommandPermissions.CONFIGURE_COMMAND_PREFIX)
    @Syntax("<prefix>")
    @Description("Change the bot command prefix")
    public void setPrefix(JDACommandEvent e, String prefix) {
        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getEvent().getGuild().getId());
        serverEntity.setCommandPrefix(prefix);
        this.getDataSuit().getServerRepository().save(serverEntity);
        this.sendMessage(e.getIssuer().getChannel(), null, "Changed command prefix to " + prefix);
    }

    @Subcommand("setwarningaction")
    @CommandPermission(CommandPermissions.CONFIGURE_WARNING_ACTION)
    @Syntax("<points> <type> <action> [timeframe]")
    @Description("Configure automated actions for specific warning points")
    public void setWarningAction(JDACommandEvent e, int points, WarningActionEntity.Type type,
                                 WarningActionEntity.Action action, @Optional TimeFrame timeFrame) {

        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getIssuer().getGuild().getId());
        WarningActionEntity warningAction = new WarningActionEntity(serverEntity, points, type,
                action, timeFrameAsString(timeFrame));

        this.getDataSuit().getWarningActionRepository().save(warningAction);
        this.sendMessage(e.getIssuer().getChannel(), null, "Action set.");
    }

    private String timeFrameAsString(@Nullable TimeFrame timeFrame) {
        if (timeFrame == null) return null;
        return timeFrame.toString();
    }

    @Subcommand("setjoinleavechannel")
    @CommandPermission(CommandPermissions.CONFIGURE_JOIN_LEAVE_CHANNEL)
    @Syntax("<channel>")
    @Description("Configure the join/leave channel")
    public void setJoinLeaveChannel(JDACommandEvent e, TextChannel textChannel) {
        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getEvent().getGuild().getId());
        serverEntity.setJoinLeaveChannel(textChannel.getId());
        this.getDataSuit().getServerRepository().save(serverEntity);
        this.sendMessage(e.getIssuer().getChannel(), null, "Channel set.");
    }

    @Subcommand("setjoinmessage")
    @CommandPermission(CommandPermissions.CONFIGURE_JOIN_MESSAGE)
    @Syntax("<message>")
    @Description("Configure the join message")
    public void setJoinMessage(JDACommandEvent e, String message) {
        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getEvent().getGuild().getId());
        serverEntity.setJoinMessage(message);
        this.getDataSuit().getServerRepository().save(serverEntity);
        this.sendMessage(e.getIssuer().getChannel(), null, "Message set.");
    }

    @Subcommand("setleavemessage")
    @CommandPermission(CommandPermissions.CONFIGURE_LEAVE_MESSASGE)
    @Syntax("<message>")
    @Description("Configure the leave message")
    public void setLeaveMessage(JDACommandEvent e, String message) {
        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getEvent().getGuild().getId());
        serverEntity.setLeaveMessage(message);
        this.getDataSuit().getServerRepository().save(serverEntity);
        this.sendMessage(e.getIssuer().getChannel(), null, "Message set.");
    }
}
