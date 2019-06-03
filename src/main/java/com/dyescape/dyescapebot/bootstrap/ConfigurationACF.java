package com.dyescape.dyescapebot.bootstrap;

import co.aikar.commands.JDACommandContexts;
import co.aikar.commands.JDACommandManager;
import com.dyescape.dyescapebot.command.discord.resolver.MemberResolver;
import com.dyescape.dyescapebot.command.discord.resolver.PermissionResolver;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
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

    /**
     * Construct a new instance of the {@link ConfigurationACF} object. This Bean class will be used to create a Bean
     * for the {@link JDACommandManager} object so that commands, argument resolvers and permission resolvers and be
     * registered.
     * @param jda {@link JDA} JDA instance
     * @author Dennis van der Veeke
     * @since 0.1.0
     */
    public ConfigurationACF(JDA jda) {
        this.jda = jda;
    }

    @Bean
    public JDACommandManager jdaCommandManager() {

        // Create our manager
        JDACommandManager commandManager = new JDACommandManager(this.jda);

        // Get the context
        JDACommandContexts contexts = (JDACommandContexts) commandManager.getCommandContexts();

        // Register all context resolvers
        contexts.registerContext(Member.class, new MemberResolver());

        // Enable unstable APIs
        commandManager.enableUnstableAPI("help");

        // Register the permission resolver
        commandManager.setPermissionResolver(new PermissionResolver());

        return commandManager;
    }
}
