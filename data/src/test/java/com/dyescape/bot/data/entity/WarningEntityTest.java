package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Warning database entity test")
public class WarningEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("testId", "!");
        UserEntity userOne = new UserEntity("userOne");
        UserEntity userTwo = new UserEntity("userTwo");
        Instant givenAt = Instant.now();
        Instant expiresAt = Instant.now().plus(Duration.ofDays(1));
        WarningEntity warning = new WarningEntity(userOne, server, 5, userTwo, givenAt, expiresAt);

        assertEquals(server, warning.getServer(), "Server value was not passed or returned correctly");
        assertEquals(server.getId(), warning.getServerId(), "Server ID value was not passed or returned correctly");
        assertEquals(userOne, warning.getUser(), "User value was not passed or returned correctly");
        assertEquals(userOne.getId(), warning.getUserId(), "User ID value was not passed or returned correctly");
        assertEquals(5, warning.getPoints(), "Points value was not passed or returned correctly");
        assertEquals(userTwo, warning.getGivenBy(), "GivenBy value was not passed or returned correctly");
        assertEquals(userTwo.getId(), warning.getGivenById(), "GivenBy ID value was not passed or returned correctly");
        assertEquals(givenAt, warning.getGivenAt(), "GivenAt value was not passed or returned correctly");
        assertEquals(expiresAt, warning.getExpiresAt(), "ExpiresAt value was not passed or returned correctly");
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
        ServerEntity server = new ServerEntity("testId", "!");
        UserEntity userOne = new UserEntity(punishedUser);
        UserEntity userTwo = new UserEntity("userTwo");
        return new WarningEntity(userOne, server, 5, userTwo, time, time);
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
