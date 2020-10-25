package com.dyescape.bot.data.entity.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "discord_server")
public class ServerEntity {

    @Id
    private String id;

    @Column(name = "command_prefix")
    private String commandPrefix = "!";

    @Column(name = "join_leave_channel")
    private String joinLeaveChannel = "";

    @Column(name = "join_message", columnDefinition = "TEXT")
    private String joinMessage = "";

    @Column(name = "leave_message", columnDefinition = "TEXT")
    private String leaveMessage = "";

    protected ServerEntity() {

    }

    public ServerEntity(String id, String commandPrefix, String joinLeaveChannel, String joinMessage, String leaveMessage) {
        this.id = id;
        this.commandPrefix = commandPrefix;
        this.joinLeaveChannel = joinLeaveChannel;
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
    }

    public String getId() {
        return this.id;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public String getJoinLeaveChannel() {
        return this.joinLeaveChannel;
    }

    public void setJoinLeaveChannel(String joinLeaveChannel) {
        this.joinLeaveChannel = joinLeaveChannel;
    }

    public String getJoinMessage() {
        return this.joinMessage;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }

    public String getLeaveMessage() {
        return this.leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerEntity that = (ServerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(commandPrefix, that.commandPrefix) &&
                Objects.equals(joinLeaveChannel, that.joinLeaveChannel) &&
                Objects.equals(joinMessage, that.joinMessage) &&
                Objects.equals(leaveMessage, that.leaveMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandPrefix, joinLeaveChannel, joinMessage, leaveMessage);
    }
}
