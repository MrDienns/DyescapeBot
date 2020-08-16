package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.WarningEntity;
import com.dyescape.bot.data.id.UserServerID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface WarningRepository extends CrudRepository<WarningEntity, UserServerID> {

    Iterable<WarningEntity> findAllByPunishmentUserIdAndPunishmentServerIdAndPunishmentGivenAtAfter(String userId,
                                                                                                    String serverId,
                                                                                                    Instant current);
}
