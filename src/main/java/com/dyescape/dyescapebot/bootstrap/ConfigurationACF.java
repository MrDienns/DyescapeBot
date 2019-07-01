package com.dyescape.dyescapebot.bootstrap;

import co.aikar.commands.JDACommandContexts;
import co.aikar.commands.JDACommandManager;
import com.dyescape.dyescapebot.command.discord.GeneralHelpCommand;
import com.dyescape.dyescapebot.command.discord.ModerationCommand;
import com.dyescape.dyescapebot.command.discord.resolver.MemberResolver;
import com.dyescape.dyescapebot.command.discord.resolver.PermissionResolver;
import com.dyescape.dyescapebot.command.discord.resolver.TextChannelResolver;
import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.repository.ModerationWarningActionRepository;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class responsible for making ACF (Annotation Command Framework) related beans, such as the
 * {@link JDACommandManager}.
 * @author Dennis van der Veeke
 * @since 0.1.0
 */
@Configuration
public class ConfigurationACF {

    private final JDA jda;
    private final Moderation moderation;
    private final ModerationWarningActionRepository warningActionRepository;

    /**
     * Construct a new instance of the {@link ConfigurationACF} object. This Bean class will be used to create a Bean
     * for the {@link JDACommandManager} object so that commands, argument resolvers and permission resolvers and be
     * registered.
     * @param jda {@link JDA} JDA instance
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public ConfigurationACF(JDA jda, Moderation moderation, ModerationWarningActionRepository warningActionRepository) {
        this.jda = jda;
        this.moderation = moderation;
        this.warningActionRepository = warningActionRepository;
    }

    @Bean
    public JDACommandManager jdaCommandManager() {

        // Create our manager
        JDACommandManager commandManager = new JDACommandManager(this.jda);

        // Get the context
        JDACommandContexts contexts = (JDACommandContexts) commandManager.getCommandContexts();

        // Register all context resolvers
        contexts.registerContext(Member.class, new MemberResolver());
        contexts.registerContext(TextChannel.class, new TextChannelResolver());

        // Enable unstable APIs
        commandManager.enableUnstableAPI("help");

        // Register the permission resolver
        commandManager.setPermissionResolver(new PermissionResolver());

        // Register the commands
        commandManager.registerCommand(new GeneralHelpCommand());
        commandManager.registerCommand(new ModerationCommand(this.moderation, this.warningActionRepository));

        return commandManager;
    }
}
