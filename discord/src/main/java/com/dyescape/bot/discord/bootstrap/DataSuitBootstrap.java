package com.dyescape.bot.discord.bootstrap;

import com.dyescape.bot.data.repository.*;
import com.dyescape.bot.data.suit.DataSuit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSuitBootstrap {

    private final AppliedRoleRepository appliedRoleRepository;
    private final RoleRepository roleRepository;
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final WarningRepository warningRepository;

    @Autowired
    public DataSuitBootstrap(AppliedRoleRepository appliedRoleRepository, RoleRepository roleRepository,
                             ServerRepository serverRepository, UserRepository userRepository,
                             WarningRepository warningRepository) {

        this.appliedRoleRepository = appliedRoleRepository;
        this.roleRepository = roleRepository;
        this.serverRepository = serverRepository;
        this.userRepository = userRepository;
        this.warningRepository = warningRepository;
    }

    @Bean
    public DataSuit getDataSuit() {
        return new DataSuit(this.appliedRoleRepository, this.roleRepository, this.serverRepository,
                this.userRepository, this.warningRepository);
    }
}
