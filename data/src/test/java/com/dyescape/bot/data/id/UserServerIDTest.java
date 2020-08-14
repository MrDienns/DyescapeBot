package com.dyescape.bot.data.id;

import com.dyescape.bot.data.entity.ServerEntity;
import com.dyescape.bot.data.entity.UserEntity;
import com.dyescape.bot.data.entity.WarningEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Server ID test")
public class UserServerIDTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800", "!");
        UserEntity user = new UserEntity("MrDienns");
        UserServerID id = new UserServerID(user.getId(), server.getId());
        assertEquals(id.getUserId(), user.getId(), "User value was not passed or returned correctly");
        assertEquals(id.getServerId(), server.getId(), "Server value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("Server1", "!");
        UserEntity userOne = new UserEntity("MrDienns");
        UserServerID id = new UserServerID(userOne.getId(), serverOne.getId());

        ServerEntity serverOneCopy = new ServerEntity("Server1", "!");
        UserEntity userOneCopy = new UserEntity("MrDienns");
        UserServerID idOneCopy = new UserServerID(userOneCopy.getId(), serverOneCopy.getId());

        ServerEntity serverTwo = new ServerEntity("Server2", "!");
        UserEntity userTwo = new UserEntity("MrDienns");
        UserServerID idTwo = new UserServerID(serverTwo.getId(), userTwo.getId());

        assertEquals(id, idOneCopy, "Equal objects were not seen as equal");
        assertEquals(id.hashCode(), idOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(id.hashCode(), idTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(id, idTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(idOneCopy, idTwo, "Unequal objects were seen as equal");
        assertNotEquals(idOneCopy.hashCode(), idTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<UserServerID> constructor = UserServerID.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("UserServerID class has no empty public/protected constructor");
        }
    }
}
