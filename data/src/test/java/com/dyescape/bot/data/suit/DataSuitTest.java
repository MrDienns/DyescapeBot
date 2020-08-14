package com.dyescape.bot.data.suit;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.entity.WarningEntity;
import com.dyescape.bot.data.repository.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

@DisplayName("DataSuit object test")
public class DataSuitTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {

        AppliedRoleRepository appliedRoleRepository = mock(AppliedRoleRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        ServerRepository serverRepository = mock(ServerRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        WarningRepository warningRepository = mock(WarningRepository.class);

        DataSuit suit = new DataSuit(appliedRoleRepository, roleRepository, serverRepository,
                userRepository, warningRepository);

        assertEquals(appliedRoleRepository, suit.getAppliedRoleRepository(), "AppliedRoleRepository value was not passed or returned correctly");
        assertEquals(roleRepository, suit.getRoleRepository(), "RoleRepository value was not passed or returned correctly");
        assertEquals(serverRepository, suit.getServerRepository(), "ServerRepository value was not passed or returned correctly");
        assertEquals(userRepository, suit.getUserRepository(), "UserRepository value was not passed or returned correctly");
        assertEquals(warningRepository, suit.getWarningRepository(), "WarningRepository value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {

        AppliedRoleRepository appliedRoleRepositoryOne = mock(AppliedRoleRepository.class);
        RoleRepository roleRepositoryOne = mock(RoleRepository.class);
        ServerRepository serverRepositoryOne = mock(ServerRepository.class);
        UserRepository userRepositoryOne = mock(UserRepository.class);
        WarningRepository warningRepositoryOne = mock(WarningRepository.class);

        AppliedRoleRepository appliedRoleRepositoryTwo = mock(AppliedRoleRepository.class);
        RoleRepository roleRepositoryTwo = mock(RoleRepository.class);
        ServerRepository serverRepositoryTwo = mock(ServerRepository.class);
        UserRepository userRepositoryTwo = mock(UserRepository.class);
        WarningRepository warningRepositoryTwo = mock(WarningRepository.class);

        DataSuit dataSuitOne = new DataSuit(appliedRoleRepositoryOne, roleRepositoryOne, serverRepositoryOne,
                userRepositoryOne, warningRepositoryOne);
        DataSuit dataSuitOneCopy = new DataSuit(appliedRoleRepositoryOne, roleRepositoryOne, serverRepositoryOne,
                userRepositoryOne, warningRepositoryOne);
        DataSuit dataSuitTwo = new DataSuit(appliedRoleRepositoryTwo, roleRepositoryTwo, serverRepositoryTwo,
                userRepositoryTwo, warningRepositoryTwo);

        assertEquals(dataSuitOne, dataSuitOneCopy, "Equal objects were not seen as equal");
        assertEquals(dataSuitOne.hashCode(), dataSuitOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(dataSuitOne, dataSuitTwo, "Unequal objects were seen as equal");
        assertNotEquals(dataSuitOne.hashCode(), dataSuitTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
        assertNotEquals(dataSuitOneCopy, dataSuitTwo, "Unequal objects were seen as equal");
        assertNotEquals(dataSuitOneCopy.hashCode(), dataSuitTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }
}
