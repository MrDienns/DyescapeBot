package com.dyescape.dyescapebot.moderation.discord;

import com.google.common.base.Strings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.PermOverrideManager;

import javax.inject.Inject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import com.dyescape.dyescapebot.exception.DyescapeBotModerationException;
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

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        this.ensureGuildHasMutedRole(guild, ensureHandler -> {

            if (ensureHandler.succeeded()) {

                if (member.getRoles().contains(ensureHandler.result())) {
                    handler.handle(Future.failedFuture(
                            new DyescapeBotModerationException(String.format("User %s is already muted.",
                                    member.getEffectiveName()))
                    ));
                    return;
                }

                GuildController guildController = new GuildController(guild);

                guildController.addRolesToMember(member, ensureHandler.result()).queue(successHandler -> {

                    this.sendPrivateMessage(userId, this.getMuteMessage(
                            this.getUsername(userId), this.getServername(serverId), reason));
                    handler.handle(Future.succeededFuture());
                }, failureHandler -> {

                    handler.handle(Future.failedFuture(failureHandler.getCause()));
                });
            } else {

                handler.handle(Future.failedFuture(ensureHandler.cause()));
            }
        });
    }

    @Override
    public void tempmute(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        this.ensureGuildHasMutedRole(guild, ensureHandler -> {

            if (ensureHandler.succeeded()) {

                if (member.getRoles().contains(ensureHandler.result())) {
                    handler.handle(Future.failedFuture(
                            new DyescapeBotModerationException(String.format("User %s is already muted.",
                                    member.getEffectiveName()))
                    ));
                    return;
                }

                GuildController guildController = new GuildController(guild);

                guildController.addRolesToMember(member, ensureHandler.result()).queue(successHandler -> {

                    this.sendPrivateMessage(userId, this.getTempMuteMessage(
                            this.getUsername(userId), this.getServername(serverId), reason, punishmentTime));
                    handler.handle(Future.succeededFuture());
                }, failureHandler -> {

                    handler.handle(Future.failedFuture(failureHandler.getCause()));
                });
            } else {

                handler.handle(Future.failedFuture(ensureHandler.cause()));
            }
        });
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

    private void ensureGuildHasMutedRole(Guild guild, Handler<AsyncResult<Role>> ensureHandler) {
        GuildController guildController = new GuildController(guild);
        List<Role> mutedRoles = guild.getRolesByName("Muted", true);

        // Some safety double checks
        if (mutedRoles.size() > 1) {

            ensureHandler.handle(Future.failedFuture(new DyescapeBotModerationException(
                    "Multiple roles named 'Muted' were found. Please ensure there is only one.")));
            return;
        }

        if (mutedRoles.isEmpty()) {

            // No muted role found, creating it
            guildController.createRole().setColor(Color.GRAY).setName("Muted").setMentionable(false)
                    .queue(successConsumer -> {

                        this.ensureMutedRolePermissions(guild, successConsumer, ensureHandler);
                    }, failureConsumer -> {

                        ensureHandler.handle(Future.failedFuture(new DyescapeBotModerationException(
                                String.format("Could not create 'Muted' role. Error: %s",
                                        failureConsumer.getMessage()))));
                    });
        } else {

            // Only one role found, so let's get it
            this.ensureMutedRolePermissions(guild, mutedRoles.get(0), ensureHandler);
        }
    }

    private void ensureMutedRolePermissions(Guild guild, Role role, Handler<AsyncResult<Role>> ensureHandler) {

        List<Future> futures = new ArrayList<>();

        guild.getChannels().forEach(channel -> {

            Future future = Future.future();
            futures.add(future);

            PermissionOverride override = channel.getPermissionOverride(role);
            if (override != null) {

                this.ensurePermissionOverrideForChannel(override, ensurePermissionHandler -> {

                    if (ensurePermissionHandler.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(ensurePermissionHandler.cause());
                    }
                }, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.VOICE_SPEAK);
            } else {

                channel.createPermissionOverride(role).queue(createSuccessConsumer -> {

                    this.ensurePermissionOverrideForChannel(createSuccessConsumer, ensurePermissionHandler -> {

                        if (ensurePermissionHandler.succeeded()) {
                            future.complete();
                        } else {
                            future.fail(ensurePermissionHandler.cause());
                        }
                    }, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.VOICE_SPEAK);
                }, creatureFailureConsumer -> {

                    ensureHandler.handle(Future.failedFuture(new DyescapeBotModerationException(
                            String.format("Could not create permission override. Error: %s",
                                    creatureFailureConsumer.getMessage()))));
                });
            }
        });

        CompositeFuture.all(futures).setHandler(compositeHandler -> {

            if (compositeHandler.succeeded()) {

                ensureHandler.handle(Future.succeededFuture(role));
            } else {

                ensureHandler.handle(Future.failedFuture(compositeHandler.cause()));
            }
        });
    }

    private void ensurePermissionOverrideForChannel(PermissionOverride override, Handler<AsyncResult<Role>> handler,
                                                    Permission... permissions) {
        PermOverrideManager overrideManager = new PermOverrideManager(override);

        if (override.getDenied().contains(Permission.MESSAGE_WRITE) &&
                override.getDenied().contains(Permission.MESSAGE_ADD_REACTION) &&
                override.getDenied().contains(Permission.VOICE_SPEAK)) {

            // This channel has the correct role permissions, so let's continue (return due to lambda)
            handler.handle(Future.succeededFuture(override.getRole()));
            return;
        }

        overrideManager.deny(permissions).queue(denySuccessConsumer -> {

            // Permissions were set correctly, so let's continue (return due to lambda)
            handler.handle(Future.succeededFuture(override.getRole()));
        }, denyFailureConsumer -> {

            handler.handle(Future.failedFuture(new DyescapeBotModerationException(
                    String.format("Could not deny permissions for role. Error: %s",
                            denyFailureConsumer.getMessage()))));
        });
    }
}
