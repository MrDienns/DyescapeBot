package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.ActivePunishment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public interface ModerationActivePunishmentRepository extends CrudRepository<ActivePunishment, ActivePunishment.PunishmentKey> {

    @Transactional
    List<ActivePunishment> findAllByEndBefore(Instant instant);
}
