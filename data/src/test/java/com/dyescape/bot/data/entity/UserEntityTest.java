package com.dyescape.bot.data.entity;

import com.dyescape.bot.data.entity.data.UserEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User database entity test")
public class UserEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        UserEntity user = new UserEntity("267965217412087818");
        assertEquals(user.getId(), "267965217412087818", "ID value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        UserEntity userOne = new UserEntity("User1");
        UserEntity userOneCopy = new UserEntity("User1");
        UserEntity userTwo = new UserEntity("User2");
        assertEquals(userOne, userOneCopy, "Equal objects were not seen as equal");
        assertEquals(userOne.hashCode(), userOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(userOne, userTwo, "Unequal objects were seen as equal");
        assertNotEquals(userOne.hashCode(), userTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
        assertNotEquals(userOneCopy, userTwo, "Unequal objects were seen as equal");
        assertNotEquals(userOneCopy.hashCode(), userTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<UserEntity> constructor = UserEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("UserEntity class has no empty public/protected constructor");
        }
    }
}
