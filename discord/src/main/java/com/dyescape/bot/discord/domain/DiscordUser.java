package com.dyescape.bot.discord.domain;

import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.discord.command.model.TimeFrame;
import com.dyescape.bot.domain.model.User;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

public class DiscordUser implements User {

    private final net.dv8tion.jda.api.entities.User jdaUser;
    private final UserEntity userEntity;

    public DiscordUser(@NotNull UserEntity userEntity, @NotNull net.dv8tion.jda.api.entities.User jdaUser) {
        this.jdaUser = jdaUser;
        this.userEntity = userEntity;
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
        return false;
    }

    public void warn(int points, @Nullable String reason) {

    }

    public void kick(@Nullable String reason) {

    }

    public void mute(@Nullable TimeFrame timeFrame, @Nullable String reason) {

    }

    public void ban(@Nullable TimeFrame timeFrame, @Nullable String reason) {

    }
}
