package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Role database entity test")
public class AppliedRoleEntityEntityIDTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800");
        UserEntity user = new UserEntity("MrDienns");
        AppliedRoleEntity.ID id = new AppliedRoleEntity.ID(user.getId(), server.getId());
        assertEquals(id.getUserId(), user.getId(), "User value was not passed or returned correctly");
        assertEquals(id.getServerId(), server.getId(), "Server value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("Server1");
        UserEntity userOne = new UserEntity("MrDienns");
        AppliedRoleEntity.ID id = new AppliedRoleEntity.ID(userOne.getId(), serverOne.getId());

        ServerEntity serverOneCopy = new ServerEntity("Server1");
        UserEntity userOneCopy = new UserEntity("MrDienns");
        AppliedRoleEntity.ID idOneCopy = new AppliedRoleEntity.ID(userOneCopy.getId(), serverOneCopy.getId());

        ServerEntity serverTwo = new ServerEntity("Server2");
        UserEntity userTwo = new UserEntity("MrDienns");
        AppliedRoleEntity.ID idTwo = new AppliedRoleEntity.ID(serverTwo.getId(), userTwo.getId());

        assertEquals(id, idOneCopy, "Equal objects were not seen as equal");
        assertEquals(id.hashCode(), idOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(id.hashCode(), idTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(id, idTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(idOneCopy, idTwo, "Unequal objects were seen as equal");
        assertNotEquals(idOneCopy.hashCode(), idTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }
}
