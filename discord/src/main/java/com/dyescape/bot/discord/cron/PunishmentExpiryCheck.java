package com.dyescape.bot.discord.cron;

import com.dyescape.bot.data.entity.PunishmentEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.domain.DiscordServer;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PunishmentExpiryCheck {

    private final static Logger LOGGER = LoggerFactory.getLogger(PunishmentExpiryCheck.class);

    private final DataSuit dataSuit;
    private final JDA jda;

    @Autowired
    public PunishmentExpiryCheck(DataSuit dataSuit, JDA jda) {
        this.dataSuit = dataSuit;
        this.jda = jda;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void check() {

        LOGGER.info("Checking for expired punishments...");

        for (PunishmentEntity punishment : this.dataSuit.getPunishmentRepository()
                .findByRevokedFalseAndExpiresAtBeforeOrderByGivenAtDesc(Instant.now())) {

            if (punishment.getAction().equals(PunishmentEntity.Action.WARN)) continue;

            LOGGER.info(String.format("Found expired %s of user %s in server %s. Expiry time: %s", punishment.getAction(),
                    punishment.getUserId(), punishment.getServerId(), punishment.getExpiresAt()));

            User user = this.getUserFromPunishment(punishment);
            Server server = this.getServerFromPunishment(punishment);
            if (user == null || server == null) continue;

            if (punishment.getAction().equals(PunishmentEntity.Action.MUTE)) {
                user.unmute(server);
            } else if (punishment.getAction().equals(PunishmentEntity.Action.BAN)) {
                user.unban(server);
            }

            LOGGER.info(String.format("Revoked %s of user %s in server %s. Expiry time: %s", punishment.getAction(),
                    punishment.getUserId(), punishment.getServerId(), punishment.getExpiresAt()));
        }

        LOGGER.info("Finished checking for expired punishments...");
    }

    private User getUserFromPunishment(PunishmentEntity punishment) {
        net.dv8tion.jda.api.entities.User jdaUser = this.jda.getUserById(punishment.getUserId());
        if (jdaUser == null) {
            jdaUser = this.jda.retrieveUserById(punishment.getUserId()).complete();
            if (jdaUser == null) return null;
        }
        return new DiscordUser(this.dataSuit, punishment.getUser(), jdaUser);
    }

    private Server getServerFromPunishment(PunishmentEntity punishment) {
        Guild guild = this.jda.getGuildById(punishment.getServerId());
        if (guild == null) return null;
        return new DiscordServer(punishment.getServer(), guild);
    }
}
