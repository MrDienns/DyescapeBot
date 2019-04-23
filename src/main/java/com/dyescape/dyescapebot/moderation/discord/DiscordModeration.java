package com.dyescape.dyescapebot.moderation.discord;

import com.google.common.base.Strings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.managers.GuildController;

import javax.inject.Inject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.util.TimeUtil;

public class DiscordModeration implements Moderation {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final JDA jda;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    @Inject
    public DiscordModeration(JDA jda) {
        this.jda = jda;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void warn(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler) {
        this.sendPrivateMessage(userId, this.getWarnMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void kick(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        GuildController guildController = new GuildController(guild);

        // Kick the user, await async response
        guildController.kick(guild.getMemberById(userId), reason).queue(successConsumer -> {

            // Succeeded, let's send a message to the user
            this.sendPrivateMessage(userId, this.getKickMessage(
                    this.getUsername(userId), this.getServername(serverId), reason));

            // And complete our Future
            handler.handle(Future.succeededFuture());
        }, failureConsumer -> {

            // Something went wrong, fail our Future
            handler.handle(Future.failedFuture(failureConsumer.getCause()));
        });
    }

    @Override
    public void mute(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler) {
        this.sendPrivateMessage(userId, this.getMuteMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void tempmute(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {
        this.sendPrivateMessage(userId, this.getTempMuteMessage(
                this.getUsername(userId), this.getServername(serverId), reason, punishmentTime));
    }

    @Override
    public void ban(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler) {
        this.sendPrivateMessage(userId, this.getBanMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void tempban(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {
        this.sendPrivateMessage(userId, this.getTempBanMessage(
                this.getUsername(userId), this.getServername(serverId), reason, punishmentTime));
    }

    // -------------------------------------------- //
    // PRIVATE
    // -------------------------------------------- //

    private void sendPrivateMessage(long userId, String message) {

        // TODO: Check if the user has PMs disabled
        this.jda.getUserById(userId).openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
        });
    }

    private String getUsername(Long userId) {
        return this.jda.getUserById(userId).getName();
    }

    private String getServername(Long serverId) {
        return this.jda.getGuildById(serverId).getName();
    }

    private String getWarnMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been warned on %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        // TODO: Inform user about how many warnings he/she has, and inform them about
        //      automated actions taken when received a certain amount of points.

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getKickMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been kicked from %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getMuteMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been muted on %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getTempMuteMessage(String username, String servername, String reason, long punishmentTime) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been muted on %s for %s.\n",
                servername, TimeUtil.parsePunishmentTime(punishmentTime)));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getBanMessage(String username, String servername, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been banned from %s.\n", servername));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getTempBanMessage(String username, String servername, String reason, long punishmentTime) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been temporarily banned from %s for %s.\n",
                servername, TimeUtil.parsePunishmentTime(punishmentTime)));
        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private StringBuilder getStringBuilder(String username) {
        return new StringBuilder(String.format("Dear %s,\n\n", username));
    }
}
