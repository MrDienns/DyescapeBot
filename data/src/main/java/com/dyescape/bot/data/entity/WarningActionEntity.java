package com.dyescape.bot.data.entity;

import com.dyescape.bot.data.id.ServerPointsID;

import javax.persistence.*;

import java.util.Objects;

@Entity
@IdClass(ServerPointsID.class)
@Table(name = "discord_warning_action")
public class WarningActionEntity {

    @Id
    @Column(name = "server_id")
    private String serverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private ServerEntity server;

    @Id
    @Column(name = "points")
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, insertable = false, columnDefinition = "enum('KICK', 'MUTE', 'BAN')")
    private Action action;

    @Column(name = "time_frame", insertable = false)
    private String timeFrame;

    protected WarningActionEntity() {

    }

    public WarningActionEntity(ServerEntity serverEntity, int points, Action action, String timeFrame) {
        this.serverId = serverEntity.getId();
        this.server = serverEntity;
        this.points = points;
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
                action == that.action &&
                Objects.equals(timeFrame, that.timeFrame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, server, points, action, timeFrame);
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
}
