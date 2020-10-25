package com.dyescape.bot.discord.command;

import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.entity.data.UserEntity;
import com.dyescape.bot.data.suit.DataSuit;
import com.dyescape.bot.discord.domain.DiscordServer;
import com.dyescape.bot.discord.domain.DiscordUser;
import com.dyescape.bot.discord.util.DiscordMessage;
import com.dyescape.bot.domain.model.Server;
import com.dyescape.bot.domain.model.User;

import co.aikar.commands.BaseCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

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

    /**
     * Send a packet which sends a "is now typing..." message in the channel.
     * @param channel Channel to send the packet in.
     */
    protected void markProcessing(MessageChannel channel) {
        channel.sendTyping().queue();
    }

    /**
     * Sends an embedded message in the provided channel.
     * @param channel   The channel to send the message in.
     * @param user      The user which the message is directed to.
     * @param body      The body of the message.
     */
    protected void sendMessage(MessageChannel channel, User user, String body) {
        net.dv8tion.jda.api.entities.User jdaUser = null;
        if (user != null) {
            jdaUser = this.getJda().getUserById(user.getId());
        }
        MessageEmbed embed = DiscordMessage.CreateEmbeddedMessage(null, body, jdaUser);
        channel.sendMessage(embed).queue();
    }

    /**
     * Sends an error response in the given channel.
     * @param channel   The channel to send the message in.
     * @param e         The exception which caused the error.
     */
    protected void error(MessageChannel channel, Exception e) {
        MessageEmbed embed = DiscordMessage.CreateEmbeddedMessage("**An error occurred**", e.getMessage(), null);
        channel.sendMessage(embed).queue();
    }
}
