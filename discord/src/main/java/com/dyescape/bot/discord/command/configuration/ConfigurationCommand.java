package com.dyescape.bot.discord.command.configuration;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.WarningActionEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.BotCommand;
import com.dyescape.bot.discord.command.CommandPermissions;
import com.dyescape.bot.domain.model.TimeFrame;

import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.*;
import net.dv8tion.jda.api.JDA;
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
        java.util.Optional<ServerEntity> query = this.getDataSuit().getServerRepository().findById(e.getEvent().getGuild().getId());
        ServerEntity serverEntity;
        if (query.isEmpty()) {
            serverEntity = new ServerEntity(e.getEvent().getGuild().getId(), prefix);
        } else {
            serverEntity = query.get();
        }
        serverEntity.setCommandPrefix(prefix);
        this.getDataSuit().getServerRepository().save(serverEntity);
        this.sendMessage(e.getIssuer().getChannel(), null, "Changed command prefix to " + prefix);
    }

    @Subcommand("setwarningaction")
    @CommandPermission(CommandPermissions.CONFIGURE_WARNING_ACTION)
    @Syntax("<points> <action> [timeframe]")
    @Description("Configure automated actions for specific warning points")
    public void setWarningAction(JDACommandEvent e, int points, WarningActionEntity.Action action,
                                 @Optional TimeFrame timeFrame) {

        ServerEntity serverEntity = this.getDataSuit().getOrCreateServerById(e.getIssuer().getGuild().getId());
        WarningActionEntity warningAction = new WarningActionEntity(serverEntity, points, action,
                timeFrameAsString(timeFrame));

        this.getDataSuit().getWarningActionRepository().save(warningAction);
        this.sendMessage(e.getIssuer().getChannel(), null, "Action set.");
    }

    private String timeFrameAsString(@Nullable TimeFrame timeFrame) {
        if (timeFrame == null) return null;
        return timeFrame.toString();
    }
}
