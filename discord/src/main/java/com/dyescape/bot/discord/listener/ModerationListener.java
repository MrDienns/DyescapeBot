package com.dyescape.bot.discord.listener;

import com.dyescape.bot.data.entity.data.PunishmentEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.domain.DiscordServer;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class ModerationListener extends ListenerAdapter {

    private final DataSuit dataSuit;

    public ModerationListener(DataSuit dataSuit) {
        this.dataSuit = dataSuit;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        // Check if the user has active mutes
        if (!this.dataSuit.getPunishmentRepository()
                .findByServerIdAndUserIdAndActionAndRevokedFalseAndExpiresAtAfterOrderByGivenAtDesc(
                        event.getGuild().getId(), event.getUser().getId(), PunishmentEntity.Action.MUTE,
                        Instant.now()).isEmpty()) {

            // Get the user
            User user = new DiscordUser(this.dataSuit, this.dataSuit.getOrCreateUserById(event.getUser().getId()),
                    event.getUser());

            // And apply the mute
            user.effectuateMute(new DiscordServer(this.dataSuit.getOrCreateServerById(event.getGuild().getId()),
                    event.getGuild()));
        }
    }
}
