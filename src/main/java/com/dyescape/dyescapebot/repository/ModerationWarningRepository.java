package com.dyescape.dyescapebot.repository;

import com.dyescape.dyescapebot.entity.discord.Warning;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ModerationWarningRepository extends CrudRepository<Warning, Long> {

    @Transactional
    List<Warning> findByServerAndUser(@Param("server_id") long serverId, @Param("user_id") long userId);

    @Transactional
    List<Warning> deleteByServerAndUser(@Param("server_id") long serverId, @Param("user_id") long userId);

    @Transactional
    List<Warning> deleteByServerAndUserAndId(@Param("server_id") long serverId, @Param("user_id") long userId,
                                             @Param("id") long warningId);
}
