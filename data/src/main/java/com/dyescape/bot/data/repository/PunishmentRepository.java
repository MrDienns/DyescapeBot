package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.PunishmentEntity;
import com.dyescape.bot.data.id.UserServerID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PunishmentRepository extends CrudRepository<PunishmentEntity, UserServerID> {

    PunishmentEntity findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(String userId, String serverId,
                                                                           PunishmentEntity.Action action);
}
