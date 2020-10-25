package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.data.ServerEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends CrudRepository<ServerEntity, String> {

}
