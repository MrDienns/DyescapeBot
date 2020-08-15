package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.WarningActionEntity;
import com.dyescape.bot.data.id.ServerPointsID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarningActionRepository extends CrudRepository<WarningActionEntity, ServerPointsID> {

}
