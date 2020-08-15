package com.dyescape.bot.data.id;

import javax.persistence.Column;
import javax.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class ServerPointsID implements Serializable {

    @Id
    @Column(name = "server_id", nullable = false, insertable = false, updatable = false)
    private String serverId;

    @Id
    @Column(name = "points", nullable = false, insertable = false, updatable = false)
    private int points;

    protected ServerPointsID() {

    }

    public ServerPointsID(String serverId, int points) {
        this.serverId = serverId;
        this.points = points;
    }

    public String getServerId() {
        return this.serverId;
    }

    public int getPoints() {
        return this.points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerPointsID that = (ServerPointsID) o;
        return points == that.points &&
                Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, points);
    }
}
