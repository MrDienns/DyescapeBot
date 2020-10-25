package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.data.AppliedRoleEntity;
import com.dyescape.bot.data.id.UserServerID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppliedRoleRepository extends CrudRepository<AppliedRoleEntity, UserServerID> {

}
