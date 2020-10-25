package com.dyescape.bot.data.repository;

import com.dyescape.bot.data.entity.data.UserEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, String> {

}
