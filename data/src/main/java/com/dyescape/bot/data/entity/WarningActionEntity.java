package com.dyescape.bot.data.entity;

import com.dyescape.bot.data.id.ServerPointsTypeID;

import javax.persistence.*;

import java.util.Objects;

@Entity
@IdClass(ServerPointsTypeID.class)
@Table(name = "discord_warning_action")
public class WarningActionEntity {

    @Id
    @Column(name = "server_id")
    private String serverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ServerEntity server;

    @Id
    @Column(name = "points")
    private int points;

    @Id
    @Column(name = "type", nullable = false, columnDefinition = "enum('DIRECT', 'FILLER')")
    @Enumerated(value = EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, columnDefinition = "enum('KICK', 'MUTE', 'BAN')")
    private Action action;

    @Column(name = "time_frame")
    private String timeFrame;

    protected WarningActionEntity() {

    }

    public WarningActionEntity(ServerEntity serverEntity, int points, Type type, Action action, String timeFrame) {
        this.serverId = serverEntity.getId();
        this.server = serverEntity;
        this.points = points;
        this.type = type;
        this.action = action;
        this.timeFrame = timeFrame;
    }

    public String getServerId() {
        return this.serverId;
    }

    public ServerEntity getServer() {
        return this.server;
    }

    public int getPoints() {
        return this.points;
    }

    public Type getType() {
        return this.type;
    }

    public Action getAction() {
        return this.action;
    }

    public String getTimeFrame() {
        return this.timeFrame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningActionEntity that = (WarningActionEntity) o;
        return points == that.points &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(server, that.server) &&
                type == that.type &&
                action == that.action &&
                Objects.equals(timeFrame, that.timeFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, server, points, type, action, timeFrame);
    }

    public enum Action {

        MUTE(true),
        KICK(false),
        BAN(true),

        ;

        private final boolean supportsTimeFrame;

        Action(boolean supportsTimeFrame) {
            this.supportsTimeFrame = supportsTimeFrame;
        }

        public boolean supportsTimeFrame() {
            return this.supportsTimeFrame;
        }
    }

    public enum Type {

        DIRECT,
        FILLER,

        ;
    }
}
