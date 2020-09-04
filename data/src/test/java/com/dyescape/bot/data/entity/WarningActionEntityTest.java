package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WarningAction database entity test")
public class WarningActionEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("id", "!", null, null, null);
        WarningActionEntity warningAction = new WarningActionEntity(server, 10, WarningActionEntity.Type.DIRECT,
                WarningActionEntity.Action.KICK, "1h30m");
        assertEquals(server, warningAction.getServer(), "Server value was not passed or returned correctly");
        assertEquals(warningAction.getServerId(), warningAction.getServer().getId(), "Server ID value was not passed or returned correctly");
        assertEquals(10, warningAction.getPoints(), "Points value was not passed or returned correctly");
        assertEquals(WarningActionEntity.Type.DIRECT, warningAction.getType(), "Type value was not passed or returned correctly");
        assertEquals(WarningActionEntity.Action.KICK, warningAction.getAction(), "Action value was not passed or returned correctly");
        assertEquals("1h30m", warningAction.getTimeFrame(), "TimeFrame value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("id", "!", null, null, null);
        WarningActionEntity warningActionOne = new WarningActionEntity(serverOne, 10, WarningActionEntity.Type.DIRECT,
                WarningActionEntity.Action.KICK, null);

        ServerEntity serverOneCopy = new ServerEntity("id", "!", null, null, null);
        WarningActionEntity warningActionOneCopy = new WarningActionEntity(serverOneCopy, 10, WarningActionEntity.Type.DIRECT,
                WarningActionEntity.Action.KICK, null);

        ServerEntity serverTwo = new ServerEntity("id", "!", null, null, null);
        WarningActionEntity warningActionTwo = new WarningActionEntity(serverTwo, 20, WarningActionEntity.Type.DIRECT,
                WarningActionEntity.Action.KICK, null);

        assertEquals(warningActionOne, warningActionOneCopy, "Equal objects were not seen as equal");
        assertEquals(warningActionOne.hashCode(), warningActionOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(warningActionOne, warningActionTwo, "Unequal objects were seen as equal");
        assertNotEquals(warningActionOne.hashCode(), warningActionTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
        assertNotEquals(warningActionOneCopy, warningActionTwo, "Unequal objects were seen as equal");
        assertNotEquals(warningActionOneCopy.hashCode(), warningActionTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<WarningActionEntity> constructor = WarningActionEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("WarningActionEntity class has no empty public/protected constructor");
        }
    }

    @Test
    @DisplayName("Action time frame support")
    public void actionSupportsTimeFrame() {
        assertFalse(WarningActionEntity.Action.KICK.supportsTimeFrame(), "Kick supports time frames");
        assertTrue(WarningActionEntity.Action.MUTE.supportsTimeFrame(), "Mute does not support time frames");
        assertTrue(WarningActionEntity.Action.BAN.supportsTimeFrame(), "Ban does not support time frames");
    }
}
