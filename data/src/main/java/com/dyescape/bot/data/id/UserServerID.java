package com.dyescape.bot.data.id;

import javax.persistence.Column;
import javax.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class UserServerID implements Serializable {

    @Id
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private String userId;

    @Id
    @Column(name = "server_id", nullable = false, insertable = false, updatable = false)
    private String serverId;

    protected UserServerID() {

    }

    public UserServerID(String userId, String serverId) {
        this.userId = userId;
        this.serverId = serverId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getServerId() {
        return this.serverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserServerID id = (UserServerID) o;
        return Objects.equals(userId, id.getUserId()) &&
                Objects.equals(serverId, id.getServerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serverId);
    }
}
