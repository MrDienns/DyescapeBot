package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.RoleEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, RoleEntity.ID> {

}
