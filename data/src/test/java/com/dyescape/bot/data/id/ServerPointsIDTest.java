package com.dyescape.bot.data.id;

import com.dyescape.bot.data.entity.data.WarningActionEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ServerPointsID object test")
public class ServerPointsIDTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerPointsTypeID id = new ServerPointsTypeID("server", 10, WarningActionEntity.Type.DIRECT);
        assertEquals("server", id.getServerId(), "Server ID value was not passed or returned correctly");
        assertEquals(10, id.getPoints(), "Points value was not passed or returned correctly");
        assertEquals(WarningActionEntity.Type.DIRECT, id.getType(), "Type value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerPointsTypeID id = new ServerPointsTypeID("server", 10, WarningActionEntity.Type.DIRECT);
        ServerPointsTypeID idOneCopy = new ServerPointsTypeID("server", 10, WarningActionEntity.Type.DIRECT);
        ServerPointsTypeID idTwo = new ServerPointsTypeID("server", 15, WarningActionEntity.Type.DIRECT);

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
            Constructor<ServerPointsTypeID> constructor = ServerPointsTypeID.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("ServerPointsID class has no empty public/protected constructor");
        }
    }
}
