package com.dyescape.dyescapebot.moderation.discord;

import com.google.common.base.Strings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.PermOverrideManager;

import javax.inject.Inject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        this.applyMute(guild, member, reason, handler);
    }

    @Override
    public void tempmute(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);

        this.applyMute(guild, member, reason, handler);
    }

    @Override
    public void unmute(long serverId, long userId, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        if (guild.isMember(user)) {
            Member member = guild.getMemberById(userId);

            this.ensureGuildHasMutedRole(guild, ensureHandler -> {

                if (ensureHandler.succeeded()) {

                    if (!member.getRoles().contains(ensureHandler.result())) {

                        handler.handle(Future.failedFuture(
                                new DyescapeBotModerationException(String.format("User %s#%s is not muted.",
                                        user.getName(), user.getDiscriminator()))
                        ));
                        return;
                    }

                    GuildController guildController = new GuildController(guild);

                    guildController.removeRolesFromMember(member, ensureHandler.result()).queue(successHandler -> {

                        // TODO: Remove mute from database

                        this.sendPrivateMessage(userId, this.getUnmuteMessage(
                                this.getUsername(userId), this.getServername(serverId)));
                        handler.handle(Future.succeededFuture());
                    }, failureHandler -> {

                        handler.handle(Future.failedFuture(failureHandler.getCause()));
                    });
                } else {

                    handler.handle(Future.failedFuture(ensureHandler.cause()));
                }
            });
        } else {

            // TODO: Remove mute from database
            return;
        }
    }

    @Override
    public void channelMute(long serverId, long userId, long channelId, String reason, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelMute(guild, member, channel, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelMutedMessage(
                    member.getUser().getName(), guild.getName(), reason, channel.getName()
            ));
            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void channelTempMute(long serverId, long userId, long channelId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelMute(guild, member, channel, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            // TODO: Update database

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelTempMutedMessage(
                    member.getUser().getName(), guild.getName(), channel.getName(), reason, punishmentTime
            ));
            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void channelBan(long serverId, long userId, long channelId, String reason, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelBan(guild, member, channel, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelBannedMessage(
                    member.getUser().getName(), guild.getName(), channel.getName(), reason
            ));
            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void channelTempBan(long serverId, long userId, long channelId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelBan(guild, member, channel, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            // TODO: Update database

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelTempBannedMessage(
                    member.getUser().getName(), guild.getName(), channel.getName(), reason, punishmentTime
            ));
            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void ban(long serverId, long userId, String reason, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        this.applyBan(guild, user, reason, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            // TODO: Update database

            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void tempban(long serverId, long userId, String reason, long punishmentTime, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        this.applyBan(guild, user, reason, banHandler -> {

            if (banHandler.failed()) {
                handler.handle(Future.failedFuture(banHandler.cause()));
                return;
            }

            // TODO: Update database

            handler.handle(Future.succeededFuture());
        });
    }

    @Override
    public void unban(long serverId, long userId, Handler<AsyncResult<Void>> handler) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        guild.getBan(user).queue(isBannedSuccess -> {

            GuildController guildController = new GuildController(guild);
            guildController.unban(user).queue(unbanSuccess -> {

                this.sendPrivateMessage(userId, this.getUnbanMessage(
                        user.getName(), guild.getName()));
            }, unbanFailure -> {

                handler.handle(Future.failedFuture(unbanFailure));
            });
        }, isBannedFailure -> {

            if (isBannedFailure.getMessage().contains("Unknown Ban")) {
                handler.handle(Future.failedFuture(new DyescapeBotModerationException(
                        String.format("User %s#%s is not banned.",
                                user.getName(), user.getDiscriminator()))));
                return;
            }
        });
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

    private String getUnmuteMessage(String username, String servername) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been unmuted on %s.\n",
                servername));

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

    private String getUnbanMessage(String username, String servername) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been unbanned from %s.\n",
                servername));

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getChannelMutedMessage(String username, String servername, String channel, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been muted in #%s on %s.\n",
                channel, servername));
        builder.append(String.format("This means that you can no longer talk in the #%s channel.\n", channel));

        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getChannelTempMutedMessage(String username, String servername, String channel,
                                             String reason, long punishment) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been muted in #%s on %s for %s.\n",
                channel, servername, TimeUtil.parsePunishmentTime(punishment)));
        builder.append(String.format("This means that you can no longer talk in the #%s channel.\n", channel));

        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getChannelBannedMessage(String username, String servername, String channel, String reason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been banned from #%s on %s.\n",
                channel, servername));
        builder.append(String.format("This means that you can no longer access the #%s channel.\n", channel));

        if (!Strings.isNullOrEmpty(reason)) {
            builder.append(String.format("**Reason: **%s\n", reason));
        }

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getChannelTempBannedMessage(String username, String servername, String channel,
                                               String reason, long punishment) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("You have been temporarily banned from #%s on %s for %s.\n",
                channel, servername, TimeUtil.parsePunishmentTime(punishment)));
        builder.append(String.format("This means that you can no longer access the #%s channel.\n", channel));

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

    private void ensureGuildHasChannelMutedRoles(Guild guild, Handler<AsyncResult<Map<Channel, Role>>> ensureHandler) {

        // Define our controller
        GuildController guildController = new GuildController(guild);

        // Prepare the to-do list
        List<Future> futures = new ArrayList<>();

        // Prepare the result
        Map<Channel, Role> channelMutedRoles = new HashMap<>();

        // Loop over all channels
        for (TextChannel channel : guild.getTextChannels()) {

            Future future = Future.future();
            futures.add(future);

            String roleName = this.getChannelMutedRoleName(channel);
            List<Role> roles = guild.getRolesByName(roleName, true);
            if (!roles.isEmpty()) {

                if (roles.size() > 1) {
                    ensureHandler.handle(Future.failedFuture(new IllegalStateException(
                            "Too many roles matching name of channel-specific punishment role: " + roleName
                    )));
                }

                channelMutedRoles.put(channel, roles.get(0));
                future.complete();
            } else {
                guildController.createRole()
                        .setMentionable(false)
                        .setName(roleName)
                        .queue(successConsumer -> {

                            this.ensureChannelDenyPermissionOverrides(channel, successConsumer, overrideHandler -> {

                                if (overrideHandler.succeeded()) {
                                    channelMutedRoles.put(channel, overrideHandler.result());
                                    future.complete();
                                } else {
                                    future.fail(overrideHandler.cause());
                                }
                            }, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE);

                        }, future::fail);
            }
        }

        CompositeFuture.all(futures).setHandler(compositeHandler -> {

            if (compositeHandler.succeeded()) {
                ensureHandler.handle(Future.succeededFuture(channelMutedRoles));
            } else {
                ensureHandler.handle(Future.failedFuture(compositeHandler.cause()));
            }
        });
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

    private void ensureGuildHasChannelBannedRoles(Guild guild, Handler<AsyncResult<Map<Channel, Role>>> ensureHandler) {

        // Define our controller
        GuildController guildController = new GuildController(guild);

        // Prepare the to-do list
        List<Future> futures = new ArrayList<>();

        // Prepare the result
        Map<Channel, Role> channelMutedRoles = new HashMap<>();

        // Loop over all channels
        for (TextChannel channel : guild.getTextChannels()) {

            Future future = Future.future();
            futures.add(future);

            String roleName = this.getChannelBannedRoleName(channel);
            List<Role> roles = guild.getRolesByName(roleName, true);
            if (!roles.isEmpty()) {

                if (roles.size() > 1) {
                    ensureHandler.handle(Future.failedFuture(new IllegalStateException(
                            "Too many roles matching name of channel-specific punishment role: " + roleName
                    )));
                }

                channelMutedRoles.put(channel, roles.get(0));
                future.complete();
            } else {
                guildController.createRole()
                        .setMentionable(false)
                        .setName(roleName)
                        .queue(successConsumer -> {

                            this.ensureChannelDenyPermissionOverrides(channel, successConsumer, overrideHandler -> {

                                if (overrideHandler.succeeded()) {
                                    channelMutedRoles.put(channel, overrideHandler.result());
                                    future.complete();
                                } else {
                                    future.fail(overrideHandler.cause());
                                }
                            }, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);

                        }, future::fail);
            }
        }

        CompositeFuture.all(futures).setHandler(compositeHandler -> {

            if (compositeHandler.succeeded()) {
                ensureHandler.handle(Future.succeededFuture(channelMutedRoles));
            } else {
                ensureHandler.handle(Future.failedFuture(compositeHandler.cause()));
            }
        });
    }

    private void ensureMutedRolePermissions(Guild guild, Role role, Handler<AsyncResult<Role>> ensureHandler) {

        List<Future> futures = new ArrayList<>();

        guild.getChannels().forEach(channel -> {

            Future future = Future.future();
            futures.add(future);

            this.ensureChannelDenyPermissionOverrides(channel, role, ensurePermissionHandler -> {

                if (ensurePermissionHandler.succeeded()) {
                    future.complete();
                } else {
                    future.fail(ensurePermissionHandler.cause());
                }
            }, Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.VOICE_SPEAK);
        });

        CompositeFuture.all(futures).setHandler(compositeHandler -> {

            if (compositeHandler.succeeded()) {

                ensureHandler.handle(Future.succeededFuture(role));
            } else {

                ensureHandler.handle(Future.failedFuture(compositeHandler.cause()));
            }
        });
    }

    private void ensureChannelDenyPermissionOverrides(Channel channel, Role role,
                                                      Handler<AsyncResult<Role>> handler, Permission... permissions) {

        PermissionOverride override = channel.getPermissionOverride(role);
        if (override != null) {

            if (!override.getDenied().containsAll(Arrays.asList(permissions))) {

                this.createPermissionDenyOverride(channel, role, handler, permissions);
            } else {
                handler.handle(Future.succeededFuture());
            }
        } else {

            this.createPermissionDenyOverride(channel, role, handler, permissions);
        }
    }

    private void createPermissionDenyOverride(Channel channel, Role role, Handler<AsyncResult<Role>> handler,
                                          Permission... permissions) {

        channel.createPermissionOverride(role).setDeny(permissions).queue(denySuccessConsumer -> {

            // Permissions were set correctly, so let's continue (return due to lambda)
            handler.handle(Future.succeededFuture(role));
        }, denyFailureConsumer -> {

            handler.handle(Future.failedFuture(new DyescapeBotModerationException(
                    String.format("Could not deny permissions for role. Error: %s",
                            denyFailureConsumer.getMessage()))));
        });
    }

    private void applyBan(Guild guild, User user, String reason, Handler<AsyncResult<Void>> handler) {
        guild.getBan(user).queue(isBannedSuccess -> {

            if (isBannedSuccess != null) {

                handler.handle(Future.failedFuture(
                        new DyescapeBotModerationException(String.format("User %s#%s is already banned.",
                                user.getName(), user.getDiscriminator()))
                ));
            }
        }, isBannedFailure -> {

            if (!isBannedFailure.getMessage().contains("Unknown Ban")) {
                handler.handle(Future.failedFuture(isBannedFailure));
                return;
            }

            GuildController guildController = new GuildController(guild);
            guildController.ban(user, 0, reason).queue(banSuccess -> {

                // TODO: Update database

                this.sendPrivateMessage(user.getIdLong(), this.getBanMessage(
                        user.getName(), guild.getName(), reason));

                handler.handle(Future.succeededFuture());
            }, banFailure -> {

                handler.handle(Future.failedFuture(banFailure.getCause()));
            });
        });
    }

    private void applyMute(Guild guild, Member member, String reason, Handler<AsyncResult<Void>> handler) {

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

                    this.sendPrivateMessage(member.getUser().getIdLong(), this.getMuteMessage(
                            member.getUser().getName(), guild.getName(), reason));
                    handler.handle(Future.succeededFuture());
                }, failureHandler -> {

                    handler.handle(Future.failedFuture(failureHandler.getCause()));
                });
            } else {

                handler.handle(Future.failedFuture(ensureHandler.cause()));
            }
        });
    }

    private void applyChannelMute(Guild guild, Member member, Channel channel, Handler<AsyncResult<Void>> handler) {
        this.ensureGuildHasChannelMutedRoles(guild, ensureHandler -> {

            if (ensureHandler.failed()) {
                handler.handle(Future.failedFuture(ensureHandler.cause()));
                return;
            }

            List<Role> roles = member.getRoles().stream()
                    .filter(role -> role.getName().equalsIgnoreCase(this.getChannelMutedRoleName(channel)))
                    .collect(Collectors.toList());
            if (!roles.isEmpty()) {
                handler.handle(Future.failedFuture(
                        new DyescapeBotModerationException("User is already muted in that channel.")));
                return;
            }

            Role channelMuteRole = ensureHandler.result().get(channel);

            GuildController guildController = new GuildController(guild);
            guildController.addRolesToMember(member, channelMuteRole).queue(successConsumer -> {

                handler.handle(Future.succeededFuture());
            }, failure -> {
                handler.handle(Future.failedFuture(failure));
            });
        });
    }

    private void applyChannelBan(Guild guild, Member member, Channel channel, Handler<AsyncResult<Void>> handler) {
        this.ensureGuildHasChannelBannedRoles(guild, ensureHandler -> {

            if (ensureHandler.failed()) {
                handler.handle(Future.failedFuture(ensureHandler.cause()));
                return;
            }

            List<Role> roles = member.getRoles().stream()
                    .filter(role -> role.getName().equalsIgnoreCase(this.getChannelBannedRoleName(channel)))
                    .collect(Collectors.toList());
            if (!roles.isEmpty()) {
                handler.handle(Future.failedFuture(
                        new DyescapeBotModerationException("User is already banned from that channel.")));
                return;
            }

            Role channelMuteRole = ensureHandler.result().get(channel);

            GuildController guildController = new GuildController(guild);
            guildController.addRolesToMember(member, channelMuteRole).queue(successConsumer -> {

                handler.handle(Future.succeededFuture());
            }, failure -> {
                handler.handle(Future.failedFuture(failure));
            });
        });
    }

    private String getChannelMutedRoleName(Channel channel) {
        return String.format("TCM: %s", channel.getIdLong());
    }

    private String getChannelBannedRoleName(Channel channel) {
        return String.format("TCB: %s", channel.getIdLong());
    }
}
