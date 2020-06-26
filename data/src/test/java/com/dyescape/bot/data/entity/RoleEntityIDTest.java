package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Role database entity test")
public class RoleEntityIDTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800", "!");
        RoleEntity.ID id = new RoleEntity.ID("moderator", server);
        assertEquals(id.getId(), "moderator", "ID value was not passed or returned correctly");
        assertEquals(id.getServer(), server, "Server value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("Server1", "!");
        RoleEntity.ID idOne = new RoleEntity.ID("moderator", serverOne);

        ServerEntity serverOneCopy = new ServerEntity("Server1", "!");
        RoleEntity.ID idOneCopy = new RoleEntity.ID("moderator", serverOneCopy);

        ServerEntity serverTwo = new ServerEntity("Server2", "!");
        RoleEntity.ID idTwo = new RoleEntity.ID("moderator", serverTwo);

        assertEquals(idOne, idOneCopy, "Equal objects were not seen as equal");
        assertEquals(idOne.hashCode(), idOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(idOne.hashCode(), idTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(idOne, idTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(idOneCopy, idTwo, "Unequal objects were seen as equal");
        assertNotEquals(idOneCopy.hashCode(), idTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }
}
