package com.dyescape.bot.discord.cron;

import com.dyescape.bot.data.entity.PunishmentEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.domain.DiscordServer;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PunishmentExpiryCheck {

    private final DataSuit dataSuit;
    private final JDA jda;

    @Autowired
    public PunishmentExpiryCheck(DataSuit dataSuit, JDA jda) {
        this.dataSuit = dataSuit;
        this.jda = jda;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void check() {

        for (PunishmentEntity punishment : this.dataSuit.getPunishmentRepository()
                .findByActionAndRevokedFalseAndExpiresAtBeforeOrderByGivenAtDesc(PunishmentEntity.Action.MUTE,
                        Instant.now())) {

            User user = this.getUserFromPunishment(punishment);
            Server server = this.getServerFromPunishment(punishment);
            if (user == null || server == null) continue;
            user.unmute(server);
        }

        for (PunishmentEntity punishment : this.dataSuit.getPunishmentRepository()
                .findByActionAndRevokedFalseAndExpiresAtBeforeOrderByGivenAtDesc(PunishmentEntity.Action.BAN,
                        Instant.now())) {

            User user = this.getUserFromPunishment(punishment);
            Server server = this.getServerFromPunishment(punishment);
            if (user == null || server == null) continue;
            user.unban(server);
        }
    }

    private User getUserFromPunishment(PunishmentEntity punishment) {
        net.dv8tion.jda.api.entities.User jdaUser = this.jda.getUserById(punishment.getUserId());
        if (jdaUser == null) return null;
        return new DiscordUser(this.dataSuit, punishment.getUser(), jdaUser);
    }

    private Server getServerFromPunishment(PunishmentEntity punishment) {
        Guild guild = this.jda.getGuildById(punishment.getServerId());
        if (guild == null) return null;
        return new DiscordServer(punishment.getServer(), guild);
    }
}
