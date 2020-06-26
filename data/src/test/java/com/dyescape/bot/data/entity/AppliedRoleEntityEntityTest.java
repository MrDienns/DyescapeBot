package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role database entity test")
public class AppliedRoleEntityEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800", "!");
        RoleEntity role = new RoleEntity("moderator", server);
        UserEntity user = new UserEntity("MrDienns");
        AppliedRoleEntity appliedRoleEntity = new AppliedRoleEntity(user, role);
        assertEquals(appliedRoleEntity.getUser(), user, "User value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getUser().getId(), user.getId(), "User ID value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getUserId(), user.getId(), "User ID value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getServerId(), server.getId(), "Server ID value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getRole(), role, "Role value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getRole().getId(), role.getId(), "Role ID value was not passed or returned correctly");
        assertEquals(appliedRoleEntity.getRoleId(), role.getId(), "Role ID value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity server = new ServerEntity("286476446338252800", "!");
        RoleEntity role = new RoleEntity("moderator", server);
        UserEntity userOne = new UserEntity("MrDienns");
        AppliedRoleEntity appliedRoleEntityOne = new AppliedRoleEntity(userOne, role);

        UserEntity userOneCopy = new UserEntity("MrDienns");
        AppliedRoleEntity appliedRoleEntityOneCopy = new AppliedRoleEntity(userOneCopy, role);

        UserEntity userTwo = new UserEntity("267965217412087818");
        AppliedRoleEntity appliedRoleEntityTwo = new AppliedRoleEntity(userTwo, role);

        assertEquals(appliedRoleEntityOne, appliedRoleEntityOneCopy, "Equal objects were not seen as equal");
        assertEquals(appliedRoleEntityOne.hashCode(), appliedRoleEntityOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(appliedRoleEntityOne.hashCode(), appliedRoleEntityTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(appliedRoleEntityOne, appliedRoleEntityTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(appliedRoleEntityOneCopy, appliedRoleEntityTwo, "Unequal objects were seen as equal");
        assertNotEquals(appliedRoleEntityOneCopy.hashCode(), appliedRoleEntityTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<AppliedRoleEntity> constructor = AppliedRoleEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("RoleEntity class has no empty public/protected constructor");
        }
    }
}
