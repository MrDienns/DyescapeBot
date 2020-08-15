package com.dyescape.bot.discord.command.moderation;

import com.dyescape.bot.data.entity.WarningActionEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.command.BotCommand;
import com.dyescape.bot.discord.command.CommandPermissions;
import com.dyescape.bot.discord.command.resolver.processor.TimeFrameProcessor;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.TimeFrame;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.JDACommandEvent;
import co.aikar.commands.annotation.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class ModerationCommand extends BotCommand {

    private static final Pattern TIMEFRAME = Pattern.compile("[0-9dhm]+");

    public ModerationCommand(DataSuit dataSuit) {
        super(dataSuit);
    }

    /**
     * Entry point for the JDA mute command. This method will be invoked for chat messages starting with the configured
     * command prefix and a prefixing warn command. The arguments are resolver automatically through the configured
     * JDA argument resolvers. Contains optional String parameter to specify reason for warning.
     * @param e         {@link JDACommandEvent}.
     * @param user      The user to warn.
     * @param reason    The reason for warn.
     */
    @CommandAlias("warn")
    @CommandPermission(CommandPermissions.KICK_MEMBER)
    @Syntax("<User> <points> [reason]")
    @Description("Warn a user")
    public void warn(JDACommandEvent e, User user, Integer points, @Optional String reason) {

        MessageChannel channel = e.getIssuer().getChannel();
        this.markProcessing(channel);

        Server server = this.getServerFromJDA(e.getIssuer().getGuild());
        User warner = this.getUserFromJDA(e.getIssuer().getAuthor());

        this.warn(server, user, points, warner, reason);

        // TODO: Fancy
        this.sendMessage(channel, "Warned: " + user.getActiveWarningPoints(server));
    }

    /**
     * Entry point for the JDA mute command. This method will be invoked for chat messages starting with the configured
     * command prefix and a prefixing mute command. The arguments are resolver automatically through the configured
     * JDA argument resolvers. Contains optional String parameter to specify reason for kick.
     * @param e         {@link JDACommandEvent}.
     * @param user      The user to kick.
     * @param reason    The reason for kick.
     */
    @CommandAlias("kick")
    @CommandPermission(CommandPermissions.KICK_MEMBER)
    @Syntax("<User> [reason]")
    @Description("Kick a user")
    public void kick(JDACommandEvent e, User user, @Optional String reason) {
        this.mute(user, reason);
    }

    /**
     * Entry point for the JDA mute command. This method will be invoked for chat messages starting with the configured
     * command prefix and a prefixing mute command. The arguments are resolver automatically through the configured
     * JDA argument resolvers. The String parameter represents a reason or time frame, and is optional. The function
     * will check if the first or last word in the parameter matches a time frame regex. If so, it will do a temp mute.
     * If not, it will do a permanent mute.
     * @param e                 {@link JDACommandEvent}.
     * @param user              The user to mute.
     * @param reasonOrTimeFrame The reason or time frame.
     */
    @CommandAlias("mute")
    @CommandPermission(CommandPermissions.MUTE_MEMBER)
    @Syntax("<User> [reason/timeframe]")
    @Description("Mute a user (permanently or temporarily)")
    public void mute(JDACommandEvent e, User user, @Optional String reasonOrTimeFrame) {

        // To prevent situations where people get banned permanently instead of temporarily, we use this little hack.
        // ACF doesn't exactly support two optional parameters properly.
        // If we found anything, invoke the other temp ban command.
        String foundTimeFrame = this.tryFindTimeFrame(reasonOrTimeFrame);
        if (foundTimeFrame != null && !foundTimeFrame.isEmpty()) {

            // Parse our time frame and temp ban the user.
            TimeFrame timeFrame = this.getTimeFrameFromString(foundTimeFrame);
            String trimmedReason = reasonOrTimeFrame.replace(foundTimeFrame, "").trim();
            this.tempMute(user, timeFrame, trimmedReason);
            return;
        }

        this.mute(user, reasonOrTimeFrame);
    }

    /**
     * Entry point for the JDA ban command. This method will be invoked for chat messages starting with the configured
     * command prefix and a prefixing ban command. The arguments are resolver automatically through the configured
     * JDA argument resolvers. The String parameter represents a reason or time frame, and is optional. The function
     * will check if the first or last word in the parameter matches a time frame regex. If so, it will do a temp ban.
     * If not, it will do a permanent ban.
     * @param e                 {@link JDACommandEvent}.
     * @param user              The user to ban.
     * @param reasonOrTimeFrame The reason or time frame.
     */
    @CommandAlias("ban")
    @CommandPermission(CommandPermissions.BAN_MEMBER)
    @Syntax("<User> [reason/timeframe]")
    @Description("Ban a user (permanently or temporarily)")
    public void ban(JDACommandEvent e, User user, @Optional String reasonOrTimeFrame) {

        // To prevent situations where people get banned permanently instead of temporarily, we use this little hack.
        // ACF doesn't exactly support two optional parameters properly.
        // If we found anything, invoke the other temp ban command.
        String foundTimeFrame = this.tryFindTimeFrame(reasonOrTimeFrame);
        if (foundTimeFrame != null && !foundTimeFrame.isEmpty()) {

            // Parse our time frame and temp ban the user.
            TimeFrame timeFrame = this.getTimeFrameFromString(foundTimeFrame);
            String trimmedReason = reasonOrTimeFrame.replace(foundTimeFrame, "").trim();
            this.tempBan(user, timeFrame, trimmedReason);
            return;
        }

        this.ban(user, reasonOrTimeFrame);
    }

    /**
     * Warn a user.
     * @param user      User to warn.
     * @param points    Points to warn the user for.
     * @param reason    Reason to warn the user for.
     */
    private void warn(Server server, User user, int points, User warnedBy, @Nullable String reason) {
        user.warn(server, points, reason, warnedBy);
    }

    /**
     * Kick a user.
     * @param user      User to kick.
     * @param reason    Reason to kick the user for.
     */
    private void kick(User user, @Nullable String reason) {
        // TODO
    }

    /**
     * Permanently mute a user.
     * @param user      User to mute.
     * @param reason    Reason to mute the user for.
     */
    private void mute(User user, @Nullable String reason) {
        // TODO
    }

    /**
     * Temporarily mute a user.
     * @param user      User to mute.
     * @param timeFrame Time frame to mute the user for.
     * @param reason    Reason to mute the user for.
     */
    private void tempMute(User user, TimeFrame timeFrame, @Nullable String reason) {
        // TODO
    }

    /**
     * Permanently ban a user.
     * @param user      User to ban.
     * @param reason    Reason to ban the user for.
     */
    private void ban(User user, @Nullable String reason) {
        // TODO
    }

    /**
     * Temporarily ban user.
     * @param user      User to ban.
     * @param timeFrame Time frame to ban the user for.
     * @param reason    Reason to ban the user for.
     */
    private void tempBan(User user, TimeFrame timeFrame, @Nullable String reason) {
        // TODO
    }

    /**
     * Pass a String argument, matching the {@link #TIMEFRAME} regex. It will take the String and pass it into the
     * {@link TimeFrameProcessor} to return a {@link TimeFrame}.
     * @param argument The argument containing a String formatted time frame
     * @return {@link TimeFrame}.
     */
    private TimeFrame getTimeFrameFromString(String argument) {
        return new TimeFrameProcessor().process(argument);
    }

    /**
     * Pass a large String, and return either the first, last word (split by spaces) or neither depending on whether or
     * not the first or last word matched the {@link #TIMEFRAME} regex. Found time frame String is returned.
     * @param text Text to search
     * @return {@link String}.
     */
    @Nullable
    private String tryFindTimeFrame(String text) {
        if (text == null || text.isEmpty()) return null;
        String[] split = text.split(" ");

        // Check if the first word in the reason was a time frame.
        String first = split[0];
        if (TIMEFRAME.matcher(first).matches()) {
            return first;
        }

        // If not, check if the last reason word in the reason is a time frame.
        String last = split[split.length - 1];
        if (TIMEFRAME.matcher(last).matches()) {
            return last;
        }

        // Found nothing
        return null;
    }



    private void markProcessing(MessageChannel channel) {
        channel.sendTyping().queue();
    }

    private void sendMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }
}
