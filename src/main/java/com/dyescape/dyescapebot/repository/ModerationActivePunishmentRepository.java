package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.ActivePunishment;
import org.springframework.data.repository.CrudRepository;

public interface ModerationActivePunishmentRepository extends CrudRepository<ActivePunishment, ActivePunishment.PunishmentKey> {

}
