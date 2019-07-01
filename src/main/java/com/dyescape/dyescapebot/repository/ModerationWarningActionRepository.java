package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.WarningAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ModerationWarningActionRepository extends CrudRepository<WarningAction, Long> {

    @Transactional
    WarningAction findByServerAndPoints(@Param("server_id") long serverId, @Param("points") int points);
}
