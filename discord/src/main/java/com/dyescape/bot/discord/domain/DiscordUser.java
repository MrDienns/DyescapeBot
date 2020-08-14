package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.entity.WarningEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;
import com.dyescape.bot.domain.model.impl.UserAbstract;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.Period;
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
    }

    @Override
    public int getActiveWarningPoints(Server server) {

        // TODO: Configurable
        Instant expiryTime = Instant.now().plus(Period.ofMonths(6));

        Spliterator<WarningEntity> spliterator = this.dataSuit.getWarningRepository()
                .findAllByUserIdAndServerIdAndGivenAtAfter(this.getId(), server.getId(),
                        expiryTime).spliterator();

        return StreamSupport.stream(spliterator, false)
                .map(WarningEntity::getPoints)
                .mapToInt(Integer::intValue).sum();
    }

    @Override
    public void kick(Server server, String reason) {

    }

    @Override
    public void mute(Server server, Instant expiryTime, String reason) {

    }

    @Override
    public boolean isMuted(Server server) {
        return false;
    }

    @Override
    public void ban(Server server, Instant expiryTime, String reason) {

    }

    @Override
    public boolean isBanned(Server server) {
        return false;
    }
}
