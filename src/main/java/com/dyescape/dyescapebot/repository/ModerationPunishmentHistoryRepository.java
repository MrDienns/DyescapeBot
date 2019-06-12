package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.PunishmentEntry;
import org.springframework.data.repository.CrudRepository;

public interface ModerationPunishmentHistoryRepository extends CrudRepository<PunishmentEntry, Long> {

}
