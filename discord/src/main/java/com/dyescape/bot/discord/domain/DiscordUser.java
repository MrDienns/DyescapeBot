package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.data.PunishmentEntity;
import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.entity.data.UserEntity;
import com.dyescape.bot.data.entity.data.WarningActionEntity;
import com.dyescape.bot.data.entity.data.WarningEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.util.DiscordMessage;
import com.dyescape.bot.domain.model.Punishment;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.TimeFrame;
import com.dyescape.bot.domain.model.User;
import com.dyescape.bot.domain.model.impl.UserAbstract;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DiscordUser extends UserAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscordUser.class);

    private final DataSuit dataSuit;
    private final UserEntity userEntity;
    private final net.dv8tion.jda.api.entities.User jdaUser;

    public DiscordUser(@NotNull DataSuit dataSuit, @NotNull UserEntity userEntity,
                       @NotNull net.dv8tion.jda.api.entities.User jdaUser) {
        this.dataSuit = dataSuit;
        this.userEntity = userEntity;
        this.jdaUser = jdaUser;
    }

    @Override
    public String getId() {
        return this.jdaUser.getId();
    }

    @Override
    public String getName() {
        return this.jdaUser.getName();
    }

    @Override
    public boolean hasPermission(String permissionId) {
        // TODO
        return false;
    }

    @Override
    public List<Punishment> getActivePunishment(Server server) {
        return this.dataSuit.getPunishmentRepository()
                .findByServerIdAndUserIdAndRevokedFalseOrderByGivenAtDesc(
                server.getId(), this.getId()).stream()
                .map(DiscordPunishment::new)
                .collect(Collectors.toList());
    }

    @Override
    public void unwarn(Server server) {

    }

    @Override
    public void warn(Server server, int points, String reason, User givenBy) {

        LOGGER.info(String.format("Warning user %s for %s points", this.getId(), points));

        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(server.getId());
        UserEntity givenByEntity = this.dataSuit.getOrCreateUserById(givenBy.getId());

        PunishmentEntity punishment = new PunishmentEntity(this.userEntity,  serverEntity, PunishmentEntity.Action.WARN,
                givenByEntity, Instant.now(), null, reason);
        punishment = this.dataSuit.getPunishmentRepository().save(punishment);

        WarningEntity warning = new WarningEntity(punishment, points);
        this.dataSuit.getWarningRepository().save(warning);

        int totalPoints = this.getActiveWarningPoints(server);
        WarningActionEntity applicableDirectAction = this.dataSuit.getWarningActionRepository()
                .findFirstByServerIdAndTypeAndPointsIsLessThanEqualOrderByPointsDesc(server.getId(),
                        WarningActionEntity.Type.DIRECT, totalPoints);

        if (applicableDirectAction != null && applicableDirectAction.getPoints() <= totalPoints - points) {
            applicableDirectAction = null;
        }

        if (applicableDirectAction == null) {
            applicableDirectAction = this.dataSuit.getWarningActionRepository()
                    .findFirstByServerIdAndTypeAndPointsIsLessThanEqualOrderByPointsDesc(server.getId(),
                            WarningActionEntity.Type.FILLER, totalPoints);
        }

        if (applicableDirectAction != null) {
            LOGGER.info(String.format("Found applicable punishment at %s (%s)", applicableDirectAction.getPoints(),
                    applicableDirectAction.getAction()));
            this.applyWarningAction(server, reason, applicableDirectAction, givenBy);
        } else {
            Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
            if (guild == null) return;
            String unit = points == 1 ? "point" : "points";
            String messageBody = String.format("You've been warned on %s for %s %s. Automated " +
                    "punishments may be applied if you reach a certain threshold. " +
                    "Please respect the rules and guidelines.", guild.getName(), points, unit);
            if (reason != null && !reason.isEmpty()) {
                messageBody = messageBody + "\n\nReason: " + reason;
            }
            this.trySendPrivateMessage(messageBody);
        }

    }

    @Override
    public int getActiveWarningPoints(Server server) {

        // TODO: Configurable
        Instant expiryTime = Instant.now().minus(Period.ofDays(180));

        Spliterator<WarningEntity> spliterator = this.dataSuit.getWarningRepository()
                .findAllByPunishmentUserIdAndPunishmentServerIdAndPunishmentGivenAtAfter(this.getId(), server.getId(),
                        expiryTime).spliterator();

        return StreamSupport.stream(spliterator, false)
                .map(WarningEntity::getPoints)
                .mapToInt(Integer::intValue).sum();
    }

    @Override
    public void kick(Server server, String reason, User givenBy) {

        LOGGER.info(String.format("Kicking user %s", this.getId()));

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        if (!guild.isMember(this.jdaUser)) {
            throw new IllegalStateException("User is not part of this server, cannot be kicked.");
        }

        String messageBody = String.format("You've been kicked from %s. You can still rejoin the server. Please " +
                "respect the rules and guidelines.", guild.getName());
        if (reason != null && !reason.isEmpty()) {
            messageBody = messageBody + "\n\nReason: " + reason;
        }
        this.trySendPrivateMessage(messageBody);

        guild.kick(this.jdaUser.getId(), reason).queue();
    }

    @Override
    public void unmute(Server server) {

        LOGGER.info(String.format("Unmuting user %s", this.getId()));

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;
        List<Role> mutedRoles = guild.getRolesByName("muted", true);
        if (!mutedRoles.isEmpty()) {
            guild.removeRoleFromMember(this.jdaUser.getId(), mutedRoles.get(0)).queue();
        }

        PunishmentEntity lastMute = this.dataSuit.getPunishmentRepository()
                .findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(this.getId(),
                        server.getId(), PunishmentEntity.Action.MUTE);

        if (lastMute == null || lastMute.isRevoked()) {
            throw new IllegalStateException("User is not muted.");
        }

        lastMute.setExpiresAt(Instant.now());
        lastMute.setRevoked(true);
        this.dataSuit.getPunishmentRepository().save(lastMute);

        String messageBody = String.format("You've been unmuted on %s. You may chat again. Please " +
                "respect the rules and guidelines.", guild.getName());
        this.trySendPrivateMessage(messageBody);
    }

    @Override
    public void mute(Server server, TimeFrame timeFrame, String reason, User givenBy) {

        LOGGER.info(String.format("Muting user %s", this.getId()));

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        this.effectuateMute(server);

        PunishmentEntity lastMute = this.dataSuit.getPunishmentRepository()
                .findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(this.getId(),
                        server.getId(), PunishmentEntity.Action.MUTE);

        if (lastMute != null && (lastMute.getExpiresAt() == null || lastMute.getExpiresAt().isAfter(Instant.now()))) {
            throw new IllegalStateException("User is already muted.");
        }

        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(server.getId());
        UserEntity givenByUser = this.dataSuit.getOrCreateUserById(givenBy.getId());

        Instant expiry = null;
        if (timeFrame != null) {
            expiry = Instant.now().plus(timeFrame.getDuration());
        }

        PunishmentEntity punishment = new PunishmentEntity(this.userEntity, serverEntity, PunishmentEntity.Action.MUTE,
                givenByUser, Instant.now(), expiry, reason);
        this.dataSuit.getPunishmentRepository().save(punishment);

        String messageBody;
        if (timeFrame != null) {
            messageBody = String.format("You've been temporarily muted on %s for %s. Your chat privileges have been revoked. Please " +
                    "respect the rules and guidelines.", guild.getName(), timeFrame.asMessage());
        } else {
            messageBody = String.format("You've been muted on %s. Your chat privileges have been revoked. Please " +
                    "respect the rules and guidelines.", guild.getName());
        }
        this.trySendPrivateMessage(messageBody);
    }

    @Override
    public void effectuateMute(Server server) {
        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) {
            LOGGER.info(String.format("Cannot apply mute guild %s isn't found.", server.getId()));
            return;
        }

        Member member = guild.getMemberById(this.jdaUser.getId());
        if (member != null) {
            Role mutedRole = this.getOrCreateMutedRole(guild);
            guild.addRoleToMember(member, mutedRole).queue();
        } else {
            LOGGER.info(String.format("Cannot apply mute right away because user %s is not in server %s",
                    this.getId(), server.getId()));
        }
    }

    @Override
    public boolean isMuted(Server server) {
        return false;
    }

    @Override
    public void unban(Server server) {

        LOGGER.info(String.format("Unbanning user %s", this.getId()));

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;
        guild.unban(this.jdaUser).queue();

        PunishmentEntity lastBan = this.dataSuit.getPunishmentRepository()
                .findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(this.getId(),
                        server.getId(), PunishmentEntity.Action.BAN);

        if (lastBan == null || lastBan.isRevoked()) {
            throw new IllegalStateException("User is not banned.");
        }

        lastBan.setExpiresAt(Instant.now());
        lastBan.setRevoked(true);
        this.dataSuit.getPunishmentRepository().save(lastBan);
    }

    @Override
    public void ban(Server server, TimeFrame timeFrame, String reason, User givenBy) {

        LOGGER.info(String.format("Banning user %s", this.getId()));

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        guild.ban(this.jdaUser, 0, reason).queue();

        PunishmentEntity lastBan = this.dataSuit.getPunishmentRepository()
                .findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(this.getId(),
                        server.getId(), PunishmentEntity.Action.BAN);

        if (lastBan != null && (lastBan.getExpiresAt() == null || lastBan.getExpiresAt().isAfter(Instant.now()))) {
            throw new IllegalStateException("User is already banned.");
        }

        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(server.getId());
        UserEntity givenByUser = this.dataSuit.getOrCreateUserById(givenBy.getId());

        PunishmentEntity punishment = new PunishmentEntity(this.userEntity, serverEntity, PunishmentEntity.Action.BAN,
                givenByUser, Instant.now(), this.getExpiryTime(timeFrame), reason);
        this.dataSuit.getPunishmentRepository().save(punishment);

        String messageBody;
        if (timeFrame != null) {
            messageBody = String.format("You've been temporarily banned from %s for %s. Please " +
                    "respect the rules and guidelines.", guild.getName(), timeFrame.asMessage());
        } else {
            messageBody = String.format("You've been banned from %s. Please " +
                    "respect the rules and guidelines.", guild.getName());
        }
        this.trySendPrivateMessage(messageBody);
    }

    @Override
    public boolean isBanned(Server server) {
        return false;
    }

    public net.dv8tion.jda.api.entities.User getJdaUser() {
        return this.jdaUser;
    }

    private void applyWarningAction(Server server, String reason, WarningActionEntity warningActionEntity, User givenBy) {

        switch (warningActionEntity.getAction()) {
            case KICK:
                this.kick(server, reason, givenBy);
                return;
            case MUTE:
                this.mute(server, tryMakeTimeFrame(warningActionEntity.getTimeFrame()), reason, givenBy);
                return;
            case BAN:
                this.ban(server, tryMakeTimeFrame(warningActionEntity.getTimeFrame()), reason, givenBy);
                return;
            default:
                break;
        }
    }

    private TimeFrame tryMakeTimeFrame(@Nullable String format) {
        if (format == null) return null;
        return new TimeFrame(format);
    }

    private Role getOrCreateMutedRole(Guild guild) {

        List<Role> roles = guild.getRolesByName("Muted", true);
        if (roles.isEmpty()) {
            return this.createMutedRole(guild);
        }

        // Not sure why you'd want multiple roles called muted.
        return roles.get(0);
    }

    private Role createMutedRole(Guild guild) {

        Role role = guild.createRole()
                .setName("Muted")
                .setColor(Color.GRAY)
                .complete();

        guild.modifyRolePositions()
                .selectPosition(role)
                .moveTo(1)
                .queue();

        for (TextChannel textChannel : guild.getTextChannels()) {
            textChannel.createPermissionOverride(role)
                    .deny(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE)
                    .queue();
        }

        return role;
    }

    private void trySendPrivateMessage(String messageText) {
        try {
            PrivateChannel privateChannel = this.jdaUser.openPrivateChannel().complete();
            if (privateChannel != null) {
                MessageEmbed message = DiscordMessage.CreateEmbeddedMessage(null, messageText, this.jdaUser);
                privateChannel.sendMessage(message).complete();
            }
        } catch (Exception ignored) { }
    }

    private Instant getExpiryTime(TimeFrame timeFrame) {
        if (timeFrame == null) return null;
        return Instant.now().plus(timeFrame.getDuration());
    }
}
