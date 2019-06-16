package com.dyescape.dyescapebot.moderation.discord;

import com.dyescape.dyescapebot.entity.discord.ActivePunishment;
import com.dyescape.dyescapebot.entity.discord.PunishmentEntry;
import com.dyescape.dyescapebot.entity.discord.Warning;
import com.dyescape.dyescapebot.exception.DyescapeBotModerationException;
import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.repository.ModerationActivePunishmentRepository;
import com.dyescape.dyescapebot.repository.ModerationPunishmentHistoryRepository;
import com.dyescape.dyescapebot.repository.ModerationWarningActionRepository;
import com.dyescape.dyescapebot.repository.ModerationWarningRepository;
import com.dyescape.dyescapebot.util.TimeUtil;
import com.google.common.base.Strings;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DiscordModeration implements Moderation {

    // -------------------------------------------- //
    // DEPENDENCIES
    // -------------------------------------------- //

    private final JDA jda;
    private final ModerationActivePunishmentRepository punishmentRepository;
    private final ModerationWarningActionRepository warningActionRepository;
    private final ModerationPunishmentHistoryRepository punishmentHistoryRepository;
    private final ModerationWarningRepository warningRepository;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DiscordModeration(JDA jda, ModerationActivePunishmentRepository punishmentRepository,
                             ModerationWarningActionRepository warningActionRepository,
                             ModerationPunishmentHistoryRepository punishmentHistoryRepository,
                             ModerationWarningRepository warningRepository) {

        this.jda = jda;
        this.punishmentRepository = punishmentRepository;
        this.warningActionRepository = warningActionRepository;
        this.punishmentHistoryRepository = punishmentHistoryRepository;
        this.warningRepository = warningRepository;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void warn(long serverId, long userId, String reason, long punisher, long time) {
        Warning warning = new Warning(serverId, userId, "WARNING", 0, reason, null, punisher);
        this.warningRepository.save(warning);

        PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "WARNING", 0, reason,
                time, punisher);
        this.punishmentHistoryRepository.save(punishmentEntry);

        this.sendPrivateMessage(userId, this.getWarnMessage(
                this.getUsername(userId), this.getServername(serverId), reason));
    }

    @Override
    public void pardon(long serverId, long userId) {

        List<Warning> warnings = this.warningRepository.findByServerAndUser(serverId, userId);

        if (warnings.isEmpty()) {

            throw new DyescapeBotModerationException("User does not have any active warnings");
        }

        this.warningRepository.deleteByServerAndUser(serverId, userId);

        this.sendPrivateMessage(userId, this.getPardonMessage(this.getUsername(userId), this.getServername(serverId)));
    }

    @Override
    public void pardon(long serverId, long userId, long warningId) {

        if (!this.warningRepository.existsById(warningId)) {

            throw new DyescapeBotModerationException("User does not have any warnings with that ID");
        }

        Warning warning = this.warningRepository.deleteByServerAndUserAndId(serverId, userId, warningId).get(0);

        this.sendPrivateMessage(userId, this.getPardonMessage(
                this.getUsername(userId), this.getServername(serverId), warning.getReason()));
    }

    @Override
    public List<Warning> getWarnings(long serverId, long userId) {
        return this.warningRepository.findByServerAndUser(serverId, userId);
    }

    @Override
    public void kick(long serverId, long userId, String reason, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        GuildController guildController = new GuildController(guild);

        // Kick the user, await async response
        guildController.kick(guild.getMemberById(userId), reason).queue(successConsumer -> {

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "KICK", 0, reason, 0, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);

            // Succeeded, let's send a message to the user
            this.sendPrivateMessage(userId, this.getKickMessage(
                    this.getUsername(userId), this.getServername(serverId), reason));
        }, failureConsumer -> {

            throw new DyescapeBotModerationException(failureConsumer.getMessage());
        });
    }

    @Override
    public void mute(long serverId, long userId, String reason, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);

        try {
            if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "MUTE", 0))) {

                ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "MUTE", 0, reason, null, punisher);
                this.punishmentRepository.save(activePunishment);

                PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "MUTE", 0, reason, 0, punisher);
                this.punishmentHistoryRepository.save(punishmentEntry);
            }

            this.applyMute(guild, member, reason).get();

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getMuteMessage(
                    member.getUser().getName(), guild.getName(), reason));
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }
    }

    @Override
    public void tempmute(long serverId, long userId, String reason, long punishmentTime, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);

        try {
            if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "MUTE", 0))) {

                Instant instant = Instant.now().plusMillis(punishmentTime);

                ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "MUTE", 0, reason, instant, punisher);
                this.punishmentRepository.save(activePunishment);

                PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "MUTE", 0, reason, punishmentTime, punisher);
                this.punishmentHistoryRepository.save(punishmentEntry);
            }

            this.applyMute(guild, member, reason).get();

            this.sendPrivateMessage(member.getUser().getIdLong(), this.getTempMuteMessage(
                    member.getUser().getName(), guild.getName(), reason, punishmentTime));
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }
    }

    @Override
    public void unmute(long serverId, long userId, long punisher) throws Exception {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        if (!guild.isMember(user)) {

            throw new DyescapeBotModerationException("Member is not part of this server.");
        }

        Member member = guild.getMemberById(userId);

        Role mutedRole = this.ensureGuildHasMutedRole(guild).get();

        ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "MUTE", 0, null, null, punisher);
        this.punishmentRepository.delete(activePunishment);

        if (!member.getRoles().contains(mutedRole)) {

            throw new DyescapeBotModerationException(String.format("User %s#%s is not muted.",
                    user.getName(), user.getDiscriminator()));
        }

        GuildController guildController = new GuildController(guild);

        guildController.removeRolesFromMember(member, mutedRole).queue(successHandler -> {

            this.sendPrivateMessage(userId, this.getUnmuteMessage(
                    this.getUsername(userId), this.getServername(serverId)));
        }, failureHandler -> {

            throw new DyescapeBotModerationException(failureHandler.getMessage());
        });
    }

    @Override
    public void channelMute(long serverId, long userId, long channelId, String reason, long punisher) {

        if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "CHANNELMUTE", channelId))) {

            ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "CHANNELMUTE", channelId, reason, null, punisher);
            this.punishmentRepository.save(activePunishment);

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "CHANNELMUTE", 0, reason, 0, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);
        }

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelMute(guild, member, channel);

        this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelMutedMessage(
                member.getUser().getName(), guild.getName(), channel.getName(), reason
        ));
    }

    @Override
    public void channelTempMute(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) {

        if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "CHANNELMUTE", channelId))) {

            Instant end = Instant.now().plusMillis(punishmentTime);

            ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "CHANNELMUTE", channelId, reason, end, punisher);
            this.punishmentRepository.save(activePunishment);

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "CHANNELMUTE", 0, reason, punishmentTime, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);
        }

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        this.applyChannelMute(guild, member, channel);

        this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelTempMutedMessage(
                member.getUser().getName(), guild.getName(), channel.getName(), reason, punishmentTime
        ));
    }

    @Override
    public void channelBan(long serverId, long userId, long channelId, String reason, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "CHANNELBAN", channelId))) {
            ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "CHANNELBAN", channelId, reason, null, punisher);
            this.punishmentRepository.save(activePunishment);

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "CHANNELBAN", 0, reason, 0, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);
        }

        this.applyChannelBan(guild, member, channel);

        this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelBannedMessage(
                member.getUser().getName(), guild.getName(), channel.getName(), reason
        ));
    }

    @Override
    public void channelTempBan(long serverId, long userId, long channelId, String reason, long punishmentTime, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        Member member = guild.getMemberById(userId);
        Channel channel = this.jda.getTextChannelById(channelId);

        if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "CHANNELBAN", channelId))) {

            Instant end = Instant.now().plusMillis(punishmentTime);

            ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "CHANNELMUTE", channelId, reason, end, punisher);
            this.punishmentRepository.save(activePunishment);

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "CHANNELBAN", 0, reason, punishmentTime, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);
        }

        this.applyChannelBan(guild, member, channel);

        this.sendPrivateMessage(member.getUser().getIdLong(), this.getChannelTempBannedMessage(
                member.getUser().getName(), guild.getName(), channel.getName(), reason, punishmentTime
        ));
    }

    @Override
    public void ban(long serverId, long userId, String reason, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        try {
            if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "BAN", 0))) {

                ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "BAN", 0, reason, null, punisher);
                this.punishmentRepository.save(activePunishment);

                PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "BAN", 0, reason, 0, punisher);
                this.punishmentHistoryRepository.save(punishmentEntry);
            }

            this.applyBan(guild, user, reason).get();

        } catch (ExecutionException | InterruptedException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }
    }

    @Override
    public void tempban(long serverId, long userId, String reason, long punishmentTime, long punisher) {

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        if (!this.punishmentRepository.existsById(new ActivePunishment.PunishmentKey(serverId, userId, "BAN", 0))) {

            Instant end = Instant.now().plusMillis(punishmentTime);

            ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "BAN", 0, reason, end, punisher);
            this.punishmentRepository.save(activePunishment);

            PunishmentEntry punishmentEntry = new PunishmentEntry(serverId, userId, "BAN", 0, reason, punishmentTime, punisher);
            this.punishmentHistoryRepository.save(punishmentEntry);
        }

        this.applyBan(guild, user, reason);
    }

    @Override
    public void unban(long serverId, long userId, long punisher) {

        ActivePunishment activePunishment = new ActivePunishment(serverId, userId, "BAN", 0, null, null, punisher);
        this.punishmentRepository.delete(activePunishment);

        // Get the guild and controller
        Guild guild = this.jda.getGuildById(serverId);
        User user = this.jda.getUserById(userId);

        guild.getBan(user).queue(isBannedSuccess -> {

            GuildController guildController = new GuildController(guild);
            guildController.unban(user).queue(unbanSuccess -> {

                this.sendPrivateMessage(userId, this.getUnbanMessage(
                        user.getName(), guild.getName()));
            }, unbanFailure -> {

                throw new DyescapeBotModerationException(unbanFailure.getMessage());
            });
        }, isBannedFailure -> {

            if (isBannedFailure.getMessage().contains("Unknown Ban")) {
                throw new DyescapeBotModerationException(
                        String.format("User %s#%s is not banned.",
                                user.getName(), user.getDiscriminator()));
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

    private String getPardonMessage(String username, String servername) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("All of your warnings on %s have been revoked by a moderator.\n", servername));

        builder.append("\n");
        builder.append("Please respect the rules and guidelines.");

        return builder.toString();
    }

    private String getPardonMessage(String username, String servername, String warningReason) {
        StringBuilder builder = this.getStringBuilder(username);

        builder.append(String.format("One of your warnings on %s has been revoked by a moderator.\n",
                servername));

        if (!Strings.isNullOrEmpty(warningReason)) {
            builder.append(String.format("**Revoked warning:** %s\n", warningReason));
        }

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

    private CompletableFuture<Map<Channel, Role>> ensureGuildHasChannelMutedRoles(Guild guild) {

        List<CompletableFuture> futures = new ArrayList<>();

        // Define our future
        CompletableFuture<Map<Channel, Role>> future = new CompletableFuture<>();

        // Define our controller
        GuildController guildController = new GuildController(guild);

        // Prepare the result
        Map<Channel, Role> channelMutedRoles = new HashMap<>();

        // Loop over all channels
        for (TextChannel channel : guild.getTextChannels()) {

            CompletableFuture completableFuture = new CompletableFuture();

            futures.add(completableFuture);

            String roleName = this.getChannelMutedRoleName(channel);
            List<Role> roles = guild.getRolesByName(roleName, true);
            if (!roles.isEmpty()) {

                if (roles.size() > 1) {
                    completableFuture.completeExceptionally(new IllegalStateException(
                            "Too many roles matching name of channel-specific punishment role: " + roleName));
                } else {
                    channelMutedRoles.put(channel, roles.get(0));
                    completableFuture.complete(null);
                }
            } else {
                guildController.createRole()
                        .setMentionable(false)
                        .setName(roleName)
                        .queue(successConsumer -> {

                            try {
                                Role role = this.ensureChannelDenyPermissionOverrides(channel, successConsumer,
                                        Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE).get();
                                channelMutedRoles.put(channel, role);

                                completableFuture.complete(null);

                            } catch (InterruptedException | ExecutionException e) {
                                throw new DyescapeBotModerationException(e.getMessage());
                            }

                        }, failure -> {

                            throw new DyescapeBotModerationException(failure.getMessage());
                        });
            }
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();

            future.complete(channelMutedRoles);
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }

        return future;
    }

    private CompletableFuture<Role> ensureGuildHasMutedRole(Guild guild) {

        CompletableFuture<Role> future = new CompletableFuture<>();

        GuildController guildController = new GuildController(guild);
        List<Role> mutedRoles = guild.getRolesByName("Muted", true);

        // Some safety double checks
        if (mutedRoles.size() > 1) {

            return CompletableFuture.failedFuture(new DyescapeBotModerationException(
                    "Multiple roles named 'Muted' were found. Please ensure there is only one."));
        }

        if (mutedRoles.isEmpty()) {

            // No muted role found, creating it
            guildController.createRole().setName("Muted").setMentionable(false)
                    .queue(successConsumer -> {

                        try {
                            future.complete(this.ensureMutedRolePermissions(guild, successConsumer).get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new DyescapeBotModerationException(e.getMessage());
                        }
                    }, failureConsumer -> {

                        throw new DyescapeBotModerationException(String.format(
                                "Could not create 'Muted' role. Error: %s",
                                failureConsumer.getMessage()));
                    });
        } else {

            // Only one role found, so let's get it
            try {
                future.complete(this.ensureMutedRolePermissions(guild, mutedRoles.get(0)).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new DyescapeBotModerationException(e.getMessage());
            }
        }

        return future;
    }

    private CompletableFuture<Map<Channel, Role>> ensureGuildHasChannelBannedRoles(Guild guild) {

        CompletableFuture<Map<Channel, Role>> returnFuture = new CompletableFuture<>();

        // Define our controller
        GuildController guildController = new GuildController(guild);

        // Prepare the to-do list
        List<CompletableFuture> futures = new ArrayList<>();

        // Prepare the result
        Map<Channel, Role> channelMutedRoles = new HashMap<>();

        // Loop over all channels
        for (TextChannel channel : guild.getTextChannels()) {

            CompletableFuture future = new CompletableFuture();
            futures.add(future);

            String roleName = this.getChannelBannedRoleName(channel);
            List<Role> roles = guild.getRolesByName(roleName, true);
            if (!roles.isEmpty()) {

                if (roles.size() > 1) {
                    throw new IllegalStateException(
                            "Too many roles matching name of channel-specific punishment role: " + roleName);
                }

                channelMutedRoles.put(channel, roles.get(0));
                future.complete(null);
            } else {
                guildController.createRole()
                        .setMentionable(false)
                        .setName(roleName)
                        .queue(successConsumer -> {

                            try {
                                Role role = this.ensureChannelDenyPermissionOverrides(channel, successConsumer,
                                        Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).get();
                                channelMutedRoles.put(channel, role);
                                future.complete(null);
                            } catch (InterruptedException | ExecutionException e) {
                                throw new DyescapeBotModerationException(e.getMessage());
                            }

                        }, failure -> {

                            throw new DyescapeBotModerationException(failure.getMessage());
                        });
            }
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();

            returnFuture.complete(channelMutedRoles);
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }

        return returnFuture;
    }

    private CompletableFuture<Role> ensureMutedRolePermissions(Guild guild, Role role) {

        List<CompletableFuture> futures = new ArrayList<>();

        guild.getChannels().forEach(channel -> {

            CompletableFuture future = new CompletableFuture();
            futures.add(future);

            try {
                this.ensureChannelDenyPermissionOverrides(channel, role,
                        Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION, Permission.VOICE_SPEAK).get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new DyescapeBotModerationException(ex.getMessage());
            }

            future.complete(null);
        });

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }

        return CompletableFuture.completedFuture(role);
    }

    private CompletableFuture<Role> ensureChannelDenyPermissionOverrides(Channel channel, Role role, Permission... permissions) {

        CompletableFuture<Role> future = new CompletableFuture<>();

        PermissionOverride override = channel.getPermissionOverride(role);
        if (override != null) {

            if (!override.getDenied().containsAll(Arrays.asList(permissions))) {

                try {
                    this.createPermissionDenyOverride(channel, role, permissions).get();
                    future.complete(role);
                } catch (InterruptedException | ExecutionException e) {
                    future.completeExceptionally(e);
                }
            } else {
                future.complete(role);
            }
        } else {

            try {
                this.createPermissionDenyOverride(channel, role, permissions).get();
                future.complete(role);
            } catch (InterruptedException | ExecutionException e) {
                future.completeExceptionally(e);
            }
        }

        return future;
    }

    private CompletableFuture createPermissionDenyOverride(Channel channel, Role role, Permission... permissions) {

        CompletableFuture completableFuture = new CompletableFuture<>();

        channel.createPermissionOverride(role).setDeny(permissions).queue(denySuccessConsumer -> {

            completableFuture.complete(null);
        }, denyFailureConsumer -> {

            completableFuture.completeExceptionally(new DyescapeBotModerationException(
                            String.format("Could not deny permissions for role. Error: %s",
                                    denyFailureConsumer.getMessage())));
        });

        return completableFuture;
    }

    private CompletableFuture applyBan(Guild guild, User user, String reason) {

        CompletableFuture future = new CompletableFuture();

        guild.getBan(user).queue(isBannedSuccess -> {

            if (isBannedSuccess != null) {

                throw new DyescapeBotModerationException(String.format("User %s#%s is already banned.",
                        user.getName(), user.getDiscriminator()));
            }
        }, isBannedFailure -> {

            if (!isBannedFailure.getMessage().contains("Unknown Ban")) {
                future.completeExceptionally(new DyescapeBotModerationException("Unknown Ban"));
            }

            GuildController guildController = new GuildController(guild);
            try {
                guildController.ban(user, 0, reason).queue(banSuccess -> {

                    // TODO: Update database

                    this.sendPrivateMessage(user.getIdLong(), this.getBanMessage(
                            user.getName(), guild.getName(), reason));

                    future.complete(null);
                }, banFailure -> {

                    future.completeExceptionally(new DyescapeBotModerationException(banFailure.getMessage()));
                });
            } catch (Exception ex) {
                future.completeExceptionally(new DyescapeBotModerationException(ex.getMessage()));
            }
        });

        return future;
    }

    private CompletableFuture applyMute(Guild guild, Member member, String reason) {

        CompletableFuture future = new CompletableFuture();

        try {
            Role role = this.ensureGuildHasMutedRole(guild).get();

            if (member.getRoles().contains(role)) {

                throw new DyescapeBotModerationException(String.format("User %s is already muted.",
                        member.getEffectiveName()));
            }

            GuildController guildController = new GuildController(guild);

            guildController.addRolesToMember(member, role).queue(successHandler -> {

                future.complete(null);
            }, failureHandler -> {

                throw new DyescapeBotModerationException(failureHandler.getMessage());
            });
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }

        return future;
    }

    private void applyChannelMute(Guild guild, Member member, Channel channel) {
        try {
            Role channelMuteRole = this.ensureGuildHasChannelMutedRoles(guild).get().get(channel);

            List<Role> roles = member.getRoles().stream()
                    .filter(role -> role.getName().equalsIgnoreCase(this.getChannelMutedRoleName(channel)))
                    .collect(Collectors.toList());

            if (!roles.isEmpty()) {
                throw new DyescapeBotModerationException("User is already muted in that channel.");
            }

            GuildController guildController = new GuildController(guild);
            guildController.addRolesToMember(member, channelMuteRole).queue(successConsumer -> {

            }, failure -> {
                throw new DyescapeBotModerationException(failure.getMessage());
            });
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }
    }

    private CompletableFuture applyChannelBan(Guild guild, Member member, Channel channel) {

        CompletableFuture future = new CompletableFuture();

        try {
            Role channelMuteRole = this.ensureGuildHasChannelBannedRoles(guild).get().get(channel);

            List<Role> roles = member.getRoles().stream()
                    .filter(role -> role.getName().equalsIgnoreCase(this.getChannelBannedRoleName(channel)))
                    .collect(Collectors.toList());
            if (!roles.isEmpty()) {
                throw new DyescapeBotModerationException("User is already banned from that channel.");
            }

            GuildController guildController = new GuildController(guild);
            guildController.addRolesToMember(member, channelMuteRole).queue(successConsumer -> {

                future.complete(null);
            }, failure -> {

                throw new DyescapeBotModerationException(failure.getMessage());
            });
        } catch (InterruptedException | ExecutionException e) {
            throw new DyescapeBotModerationException(e.getMessage());
        }

        return future;
    }

    private String getChannelMutedRoleName(Channel channel) {
        return String.format("TCM: %s", channel.getIdLong());
    }

    private String getChannelBannedRoleName(Channel channel) {
        return String.format("TCB: %s", channel.getIdLong());
    }
}