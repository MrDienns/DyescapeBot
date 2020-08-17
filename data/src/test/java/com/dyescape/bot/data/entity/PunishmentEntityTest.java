package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class PunishmentEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("testId", "!");
        UserEntity userOne = new UserEntity("userOne");
        UserEntity userTwo = new UserEntity("userTwo");
        PunishmentEntity.Action action = PunishmentEntity.Action.WARN;
        Instant givenAt = Instant.now();

        PunishmentEntity punishment = new PunishmentEntity(userOne, server, action, userTwo, givenAt, null, "test");
        Instant expiresAt = givenAt.plusSeconds(300);
        punishment.setExpiresAt(expiresAt);

        assertNull(punishment.getId(), "Punishment ID should be null before saved");
        assertEquals(userOne, punishment.getUser(), "Punishment ID value was not passed or returned correctly");
        assertEquals(userOne.getId(), punishment.getUserId(), "Punishment ID value was not passed or returned correctly");
        assertEquals(server, punishment.getServer(), "Punishment ID value was not passed or returned correctly");
        assertEquals(server.getId(), punishment.getServerId(), "Punishment ID value was not passed or returned correctly");
        assertEquals(PunishmentEntity.Action.WARN, punishment.getAction(), "Punishment ID value was not passed or returned correctly");
        assertEquals(givenAt, punishment.getGivenAt(), "Punishment ID value was not passed or returned correctly");
        assertEquals(expiresAt, punishment.getExpiresAt(), "Punishment ID value was not passed or returned correctly");
        assertEquals(userTwo, punishment.getGivenBy(), "Punishment ID value was not passed or returned correctly");
        assertEquals(userTwo.getId(), punishment.getGivenById(), "Punishment ID value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        Instant testTime = Instant.now();
        PunishmentEntity punishmentOne = createTestPunishment("one", testTime);
        PunishmentEntity punishmentOneCopy = createTestPunishment("one", testTime);
        PunishmentEntity punishmentTwo = createTestPunishment("oneTwo", testTime);
        assertEquals(punishmentOne, punishmentOneCopy, "Equal objects were not seen as equal");
        assertEquals(punishmentOne.hashCode(), punishmentOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(punishmentOne, punishmentTwo, "Unequal objects were seen as equal");
        assertNotEquals(punishmentOne.hashCode(), punishmentTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
        assertNotEquals(punishmentOneCopy, punishmentTwo, "Unequal objects were seen as equal");
        assertNotEquals(punishmentOneCopy.hashCode(), punishmentTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    private PunishmentEntity createTestPunishment(String punishedUser, Instant time) {
        ServerEntity server = new ServerEntity("testId", "!");
        UserEntity userOne = new UserEntity(punishedUser);
        UserEntity userTwo = new UserEntity("userTwo");
        return new PunishmentEntity(userOne, server, PunishmentEntity.Action.WARN, userTwo, time, time, "test");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<PunishmentEntity> constructor = PunishmentEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("PunishmentEntity class has no empty public/protected constructor");
        }
    }
}
