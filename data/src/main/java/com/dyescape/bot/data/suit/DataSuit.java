package com.dyescape.bot.data.suit;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.repository.*;

import java.util.Objects;
import java.util.Optional;

public class DataSuit {

    private final AppliedRoleRepository appliedRoleRepository;
    private final RoleRepository roleRepository;
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final WarningRepository warningRepository;

    public DataSuit(AppliedRoleRepository appliedRoleRepository, RoleRepository roleRepository,
                    ServerRepository serverRepository, UserRepository userRepository,
                    WarningRepository warningRepository) {

        this.appliedRoleRepository = appliedRoleRepository;
        this.roleRepository = roleRepository;
        this.serverRepository = serverRepository;
        this.userRepository = userRepository;
        this.warningRepository = warningRepository;
    }

    public AppliedRoleRepository getAppliedRoleRepository() {
        return this.appliedRoleRepository;
    }

    public RoleRepository getRoleRepository() {
        return this.roleRepository;
    }

    public ServerRepository getServerRepository() {
        return this.serverRepository;
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    public WarningRepository getWarningRepository() {
        return this.warningRepository;
    }

    public UserEntity getOrCreateUserById(String userId) {
        Optional<UserEntity> userEntityResult = this.userRepository.findById(userId);
        UserEntity userEntity;
        if (userEntityResult.isPresent()) {
            userEntity = userEntityResult.get();
        } else {
            userEntity = new UserEntity(userId);
            this.userRepository.save(userEntity);
        }

        return userEntity;
    }

    public ServerEntity getOrCreateServerById(String serverId) {
        Optional<ServerEntity> serverEntityResult = this.serverRepository.findById(serverId);
        ServerEntity serverEntity;
        if (serverEntityResult.isPresent()) {
            serverEntity = serverEntityResult.get();
        } else {
            serverEntity = new ServerEntity(serverId, "!");
            this.serverRepository.save(serverEntity);
        }

        return serverEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSuit dataSuit = (DataSuit) o;
        return Objects.equals(appliedRoleRepository, dataSuit.appliedRoleRepository) &&
                Objects.equals(roleRepository, dataSuit.roleRepository) &&
                Objects.equals(serverRepository, dataSuit.serverRepository) &&
                Objects.equals(userRepository, dataSuit.userRepository) &&
                Objects.equals(warningRepository, dataSuit.warningRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appliedRoleRepository, roleRepository, serverRepository, userRepository, warningRepository);
    }
}
