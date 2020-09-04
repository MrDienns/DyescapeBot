package com.dyescape.bot.data.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User database entity test")
public class ServerEntityTest {

    @Test
    @DisplayName("Object creation")
    public void objectCreation() {
        ServerEntity server = new ServerEntity("286476446338252800", "!", "channel", "join", "leave");
        assertEquals(server.getId(), "286476446338252800", "ID value was not passed or returned correctly");
        assertEquals(server.getCommandPrefix(), "!", "Command prefix value was not passed or returned correctly");
        assertEquals(server.getJoinLeaveChannel(), "channel", "Join/Leave channel value was not passed or returned correctly");
        assertEquals(server.getJoinMessage(), "join", "Join message value was not passed or returned correctly");
        assertEquals(server.getLeaveMessage(), "leave", "Leave message value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object updating")
    public void objectUpdating() {
        ServerEntity server = new ServerEntity("286476446338252800", "!", "channel", "join", "leave");

        server.setJoinLeaveChannel("new_channel");
        server.setJoinMessage("new_join_message");
        server.setLeaveMessage("new_leave_message");

        assertEquals(server.getId(), "286476446338252800", "ID value was not passed or returned correctly");
        assertEquals(server.getCommandPrefix(), "!", "Command prefix value was not passed or returned correctly");
        assertEquals(server.getJoinLeaveChannel(), "new_channel", "Join/Leave channel value was not passed or returned correctly");
        assertEquals(server.getJoinMessage(), "new_join_message", "Join message value was not passed or returned correctly");
        assertEquals(server.getLeaveMessage(), "new_leave_message", "Leave message value was not passed or returned correctly");
    }

    @Test
    @DisplayName("Object comparison")
    public void objectComparison() {
        ServerEntity serverOne = new ServerEntity("Server1", "!", "channel", "join", "leave");
        ServerEntity serverOneCopy = new ServerEntity("Server1", "!", "channel", "join", "leave");
        ServerEntity serverTwo = new ServerEntity("Server2", "!", "channel", "join", "leave");
        assertEquals(serverOne, serverOneCopy, "Equal objects were not seen as equal");
        assertEquals(serverOne.hashCode(), serverOneCopy.hashCode(), "Hashcode of equal objects were not seen as equal");
        assertNotEquals(serverOne.hashCode(), serverTwo.hashCode(), "Unequal objects were seen as equal");
        assertNotEquals(serverOne, serverTwo, "Hashcode of unequal objects were seen as equal");
        assertNotEquals(serverOneCopy, serverTwo, "Unequal objects were seen as equal");
        assertNotEquals(serverOneCopy.hashCode(), serverTwo.hashCode(), "Hashcode of unequal objects were seen as equal");
    }

    @Test
    @DisplayName("Serialization constructor")
    public void serializationConstructor() {
        try {
            Constructor<ServerEntity> constructor = ServerEntity.class.getDeclaredConstructor();
            constructor.newInstance();
        } catch (Exception ignored) {
            fail("ServerEntity class has no empty public/protected constructor");
        }
    }

    @Test
    @DisplayName("Change command prefix")
    public void changeCommandPrefix() {
        ServerEntity server = new ServerEntity("Server1", "!", "channel", "join", "leave");
        server.setCommandPrefix("?");
        assertEquals("?", server.getCommandPrefix(), "Command prefix was not changed");
    }
}
