package com.dyescape.dyescapebot.bootstrap;

import com.dyescape.dyescapebot.moderation.Moderation;
import com.dyescape.dyescapebot.moderation.discord.DiscordModeration;
import com.dyescape.dyescapebot.repository.ModerationActivePunishmentRepository;
import com.dyescape.dyescapebot.repository.ModerationPunishmentHistoryRepository;
import com.dyescape.dyescapebot.repository.ModerationWarningActionRepository;
import com.dyescape.dyescapebot.repository.ModerationWarningRepository;
import net.dv8tion.jda.core.JDA;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationModeration {

    private final JDA jda;
    private final ModerationActivePunishmentRepository punishmentRepository;
    private final ModerationWarningActionRepository warningActionRepository;
    private final ModerationPunishmentHistoryRepository punishmentHistoryRepository;
    private final ModerationWarningRepository warningRepository;

    public ConfigurationModeration(JDA jda, ModerationActivePunishmentRepository punishmentRepository,
                                   ModerationWarningActionRepository warningActionRepository,
                                   ModerationPunishmentHistoryRepository punishmentHistoryRepository,
                                   ModerationWarningRepository warningRepository) {

        this.jda = jda;
        this.punishmentRepository = punishmentRepository;
        this.warningActionRepository = warningActionRepository;
        this.punishmentHistoryRepository = punishmentHistoryRepository;
        this.warningRepository = warningRepository;
    }

    @Bean
    public Moderation moderation() {
        return new DiscordModeration(this.jda, this.punishmentRepository, this.warningActionRepository,
                this.punishmentHistoryRepository, this.warningRepository);
    }
}
