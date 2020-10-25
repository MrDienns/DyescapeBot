package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.data.PunishmentEntity;
import com.dyescape.bot.data.id.UserServerID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PunishmentRepository extends CrudRepository<PunishmentEntity, UserServerID> {

    PunishmentEntity findTopByUserIdAndServerIdAndActionOrderByGivenAtDesc(String userId, String serverId,
                                                                           PunishmentEntity.Action action);

    List<PunishmentEntity> findByRevokedFalseAndExpiresAtBeforeOrderByGivenAtDesc(Instant expiry);

    List<PunishmentEntity> findByServerIdAndUserIdAndActionAndRevokedFalseAndExpiresAtAfterOrderByGivenAtDesc(
            String serverId, String userId, PunishmentEntity.Action action, Instant expiry);

    List<PunishmentEntity> findByServerIdAndUserIdAndRevokedFalseOrderByGivenAtDesc(
            String serverId, String userId);
}
