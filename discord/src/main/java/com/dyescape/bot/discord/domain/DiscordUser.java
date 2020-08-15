package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.entity.WarningActionEntity;
import com.dyescape.bot.data.entity.WarningEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.util.DiscordMessage;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.TimeFrame;
import com.dyescape.bot.domain.model.User;
import com.dyescape.bot.domain.model.impl.UserAbstract;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

public class DiscordUser extends UserAbstract {

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
    public void warn(Server server, int points, String reason, User givenBy) {

        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(server.getId());
        UserEntity givenByEntity = this.dataSuit.getOrCreateUserById(givenBy.getId());

        WarningEntity warning = new WarningEntity(this.userEntity, serverEntity, points, givenByEntity,
                Instant.now(), reason);

        this.dataSuit.getWarningRepository().save(warning);

        int totalPoints = this.getActiveWarningPoints(server);
        WarningActionEntity applicableAction = this.dataSuit.getWarningActionRepository()
                .findFirstByServerIdAndPointsIsLessThanEqualOrderByPointsDesc(server.getId(), totalPoints);

        if (applicableAction != null) {
            this.applyWarningAction(server, reason, applicableAction);
        } else {
            // TODO: Inform user
        }
    }

    @Override
    public int getActiveWarningPoints(Server server) {

        // TODO: Configurable
        Instant expiryTime = Instant.now().minus(Period.ofDays(180));

        Spliterator<WarningEntity> spliterator = this.dataSuit.getWarningRepository()
                .findAllByUserIdAndServerIdAndGivenAtAfter(this.getId(), server.getId(),
                        expiryTime).spliterator();

        return StreamSupport.stream(spliterator, false)
                .map(WarningEntity::getPoints)
                .mapToInt(Integer::intValue).sum();
    }

    @Override
    public void kick(Server server, String reason) {

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        try {
            PrivateChannel privateChannel = this.jdaUser.openPrivateChannel().complete();
            if (privateChannel != null) {
                String messageBody = String.format("You've been kicked from %s. You can still rejoin the server. Please " +
                        "respect the rules and guidelines.", guild.getName());
                if (reason != null && !reason.isEmpty()) {
                    messageBody = messageBody + "\n\nReason: " + reason;
                }
                MessageEmbed message = DiscordMessage.CreateEmbeddedMessage(null, messageBody, this.jdaUser);
                privateChannel.sendMessage(message).complete();
            }
        } catch (Exception ignored) { }

        guild.kick(this.jdaUser.getId(), reason).queue();
    }

    @Override
    public void mute(Server server, TimeFrame timeFrame, String reason) {

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        Member member = guild.getMemberById(this.jdaUser.getId());
        if (member != null) {
            // apply mute role
            Role mutedRole = this.getOrCreateMutedRole(guild);
            guild.addRoleToMember(member, mutedRole).queue();
        }

        // apply database change
    }

    @Override
    public boolean isMuted(Server server) {
        return false;
    }

    @Override
    public void ban(Server server, TimeFrame timeFrame, String reason) {

        Guild guild = this.jdaUser.getJDA().getGuildById(server.getId());
        if (guild == null) return;

        guild.ban(this.jdaUser, 0, reason).queue();
    }

    @Override
    public boolean isBanned(Server server) {
        return false;
    }

    private void applyWarningAction(Server server, String reason, WarningActionEntity warningActionEntity) {

        switch (warningActionEntity.getAction()) {
            case KICK:
                this.kick(server, reason);
                return;
            case MUTE:
                this.mute(server, tryMakeTimeFrame(warningActionEntity.getTimeFrame()), reason);
            case BAN:
                this.ban(server, tryMakeTimeFrame(warningActionEntity.getTimeFrame()), reason);
            default:
                break;
        }

        if (warningActionEntity.getAction().equals(WarningActionEntity.Action.KICK)) {
            this.kick(server, reason);
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
}
