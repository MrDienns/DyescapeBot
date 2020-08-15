package com.dyescape.bot.discord.command;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.domain.DiscordServer;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.BaseCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public abstract class BotCommand extends BaseCommand {

    private final JDA jda;
    private final DataSuit dataSuit;

    public BotCommand(JDA jda, DataSuit dataSuit) {
        this.jda = jda;
        this.dataSuit = dataSuit;
    }

    public JDA getJda() {
        return this.jda;
    }

    protected DataSuit getDataSuit() {
        return this.dataSuit;
    }

    /**
     * Takes a {@link Guild} and gives back a corresponding {@link Server}.
     * @param guild The JDA guild.
     * @return {@link Server}
     */
    protected Server getServerFromJDA(Guild guild) {
        ServerEntity serverEntity = this.dataSuit.getOrCreateServerById(guild.getId());
        return new DiscordServer(serverEntity, guild);
    }

    /**
     * Takes a {@link net.dv8tion.jda.api.entities.User} and gives back a corresponding {@link User}.
     * @param jdaUser The JDA user.
     * @return {@link User}
     */
    protected User getUserFromJDA(net.dv8tion.jda.api.entities.User jdaUser) {
        UserEntity author = this.dataSuit.getOrCreateUserById(jdaUser.getId());
        return new DiscordUser(this.dataSuit, author, jdaUser);
    }
}
