package com.dyescape.bot.data.id;

import com.dyescape.bot.data.entity.WarningActionEntity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

public class ServerPointsTypeID implements Serializable {

    @Id
    @Column(name = "server_id", nullable = false, updatable = false)
    private String serverId;

    @Id
    @Column(name = "points", nullable = false, updatable = false)
    private int points;

    @Id
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, columnDefinition = "enum('DIRECT', 'FILLER')")
    private WarningActionEntity.Type type;

    protected ServerPointsTypeID() {

    }

    public ServerPointsTypeID(String serverId, int points, WarningActionEntity.Type type) {
        this.serverId = serverId;
        this.points = points;
        this.type = type;
    }

    public String getServerId() {
        return this.serverId;
    }

    public int getPoints() {
        return this.points;
    }

    public WarningActionEntity.Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerPointsTypeID that = (ServerPointsTypeID) o;
        return points == that.points &&
                Objects.equals(serverId, that.serverId) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, points, type);
    }
}
