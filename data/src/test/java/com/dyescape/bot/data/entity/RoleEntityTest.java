package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role database entity test")
public class RoleEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800");
        RoleEntity role = new RoleEntity("moderator", server);
        assertEquals(role.getId(), "moderator", "ID value was not passed or returned correctly");
        assertEquals(role.getServer(), server, "Server value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("Server1");
        RoleEntity roleOne = new RoleEntity("moderator", serverOne);

        ServerEntity serverOneCopy = new ServerEntity("Server1");
        RoleEntity roleOneCopy = new RoleEntity("moderator", serverOneCopy);

        ServerEntity serverTwo = new ServerEntity("Server2");
        RoleEntity roleTwo = new RoleEntity("moderator", serverTwo);

        assertEquals(roleOne, roleOneCopy, "Equal objects were not seen as equal");
        assertEquals(roleOne.hashCode(), roleOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(roleOne.hashCode(), roleTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(roleOne, roleTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(roleOneCopy, roleTwo, "Unequal objects were seen as equal");
        assertNotEquals(roleOneCopy.hashCode(), roleTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<RoleEntity> constructor = RoleEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("RoleEntity class has no empty public/protected constructor");
        }
    }
}
