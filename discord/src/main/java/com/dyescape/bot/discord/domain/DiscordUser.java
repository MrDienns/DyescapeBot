package com.dyescape.bot.discord.domain;

import com.dyescape.bot.domain.model.User;

import javax.validation.constraints.NotNull;

public class DiscordUser implements User {

    private final net.dv8tion.jda.api.entities.User jdaUser;

    public DiscordUser(@NotNull net.dv8tion.jda.api.entities.User jdaUser) {
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
        return false;
    }
}
