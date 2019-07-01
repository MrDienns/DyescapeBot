package com.dyescape.dyescapebot.moderation;

import com.dyescape.dyescapebot.entity.discord.ActivePunishment;
import com.dyescape.dyescapebot.entity.discord.Warning;
import com.dyescape.dyescapebot.repository.ModerationActivePunishmentRepository;
import com.dyescape.dyescapebot.repository.ModerationWarningRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class PunishmentExpiryChecker {

    private final Moderation moderation;
    private final ModerationActivePunishmentRepository activePunishmentRepository;
    private final ModerationWarningRepository warningRepository;

    public PunishmentExpiryChecker(Moderation moderation,
                                   ModerationActivePunishmentRepository activePunishmentRepository,
                                   ModerationWarningRepository warningRepository) {

        this.moderation = moderation;
        this.activePunishmentRepository = activePunishmentRepository;
        this.warningRepository = warningRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void check() throws Exception {
        List<ActivePunishment> expiredPunishments = this.activePunishmentRepository.findAllByEndBefore(Instant.now());
        List<Warning> expiredWarnings = this.warningRepository.findAllByEndBefore(Instant.now());

        for (ActivePunishment p : expiredPunishments) {
            switch (p.getType()) {

                case MUTE:
                    this.moderation.unmute(p.getServer(), p.getUser());
                    break;
                case CHANNELMUTE:
                    this.moderation.unchannelMute(p.getServer(), p.getUser(), p.getChannel());
                    break;
                case BAN:
                    this.moderation.unban(p.getServer(), p.getUser());
                    break;
                case CHANNELBAN:
                    this.moderation.unchannelBan(p.getServer(), p.getUser(), p.getChannel());
                    break;
                default:
                    throw new IllegalStateException("Unknown punishment type " + p.getType());
            }
        }

        for (Warning w : expiredWarnings) {

            // I believe it's better to not notify a user of expired warnings from a moderation standpoint.
            this.moderation.pardon(w.getServer(), w.getUser(), w.getId(), false);
        }
    }
}
