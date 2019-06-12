package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.WarningAction;
import org.springframework.data.repository.CrudRepository;

public interface ModerationWarningActionRepository extends CrudRepository<WarningAction, Long> {

}
