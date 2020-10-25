package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.data.PunishmentEntity;
import com.dyescape.bot.domain.model.Punishment;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class DiscordPunishment implements Punishment {

    private final PunishmentEntity punishmentEntity;

    public DiscordPunishment(PunishmentEntity punishmentEntity) {
        this.punishmentEntity = punishmentEntity;
    }

    @Override
    public String getAction() {
        return this.punishmentEntity.getAction().name();
    }

    @Override
    @Nullable
    public Instant getExpiresAt() {
        return this.punishmentEntity.getExpiresAt();
    }
}
