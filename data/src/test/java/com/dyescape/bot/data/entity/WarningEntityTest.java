package com.dyescape.bot.data.entity;

import com.dyescape.bot.data.entity.data.PunishmentEntity;
import com.dyescape.bot.data.entity.data.ServerEntity;
import com.dyescape.bot.data.entity.data.UserEntity;
import com.dyescape.bot.data.entity.data.WarningEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Warning database entity test")
public class WarningEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("testId", "!", null, null, null);
        UserEntity userOne = new UserEntity("userOne");
        UserEntity userTwo = new UserEntity("userTwo");
        PunishmentEntity.Action action = PunishmentEntity.Action.WARN;
        Instant givenAt = Instant.now();

        PunishmentEntity punishment = new PunishmentEntity(userOne, server, action, userTwo, givenAt, null, "test");

        WarningEntity warning = new WarningEntity(punishment, 5);

        assertEquals(punishment.getId(), warning.getPunishmentId(), "Punishment ID value was not passed or returned correctly");
        assertEquals(punishment, warning.getPunishment(), "Punishment ID value was not passed or returned correctly");
        assertEquals(5, warning.getPoints(), "Points value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        Instant testTime = Instant.now();
        WarningEntity warningOne = createTestWarning("one", testTime);
        WarningEntity warningOneCopy = createTestWarning("one", testTime);
        WarningEntity warningTwo = createTestWarning("two", testTime);
        assertEquals(warningOne, warningOneCopy, "Equal objects were not seen as equal");
        assertEquals(warningOne.hashCode(), warningOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(warningOne, warningTwo, "Unequal objects were seen as equal");
        assertNotEquals(warningOne.hashCode(), warningTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
        assertNotEquals(warningOneCopy, warningTwo, "Unequal objects were seen as equal");
        assertNotEquals(warningOneCopy.hashCode(), warningTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    private WarningEntity createTestWarning(String punishedUser, Instant time) {
        ServerEntity server = new ServerEntity("testId", "!", null, null, null);
        UserEntity userOne = new UserEntity(punishedUser);
        UserEntity userTwo = new UserEntity("userTwo");
        PunishmentEntity punishment = new PunishmentEntity(userOne, server, PunishmentEntity.Action.WARN, userTwo, time, null, "test");
        return new WarningEntity(punishment, 5);
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<WarningEntity> constructor = WarningEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("WarningEntity class has no empty public/protected constructor");
        }
    }
}
