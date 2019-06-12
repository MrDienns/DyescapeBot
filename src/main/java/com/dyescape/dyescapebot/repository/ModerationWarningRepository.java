package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.Warning;
import org.springframework.data.repository.CrudRepository;

public interface ModerationWarningRepository extends CrudRepository<Warning, Long> {
}
