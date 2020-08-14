package com.dyescape.bot.data.entity;

import com.dyescape.bot.data.id.UserServerID;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;

@Entity
@IdClass(UserServerID.class)
@Table(name = "discord_warning")
public class WarningEntity {

    @Id
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private String userId;

    @Id
    @Column(name = "server_id", nullable = false, insertable = false, updatable = false)
    private String serverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private ServerEntity server;

    @Column(name = "points")
    private int points;

    @Column(name = "given_by_id")
    private String givenById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity givenBy;

    @Column(name = "given_at")
    private Instant givenAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    protected WarningEntity() {

    }

    public WarningEntity(UserEntity user, ServerEntity server, int points, UserEntity givenBy, Instant givenAt,
                         Instant expiresAt) {
        this.user = user;
        this.userId = user.getId();
        this.server = server;
        this.serverId = server.getId();
        this.givenBy = givenBy;
        this.givenById = givenBy.getId();
        this.points = points;
        this.givenAt = givenAt;
        this.expiresAt = expiresAt;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public ServerEntity getServer() {
        return this.server;
    }

    public int getPoints() {
        return this.points;
    }

    public String getGivenById() {
        return this.givenById;
    }

    public UserEntity getGivenBy() {
        return this.givenBy;
    }

    public Instant getGivenAt() {
        return this.givenAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningEntity that = (WarningEntity) o;
        return points == that.points &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(user, that.user) &&
                Objects.equals(server, that.server) &&
                Objects.equals(givenById, that.givenById) &&
                Objects.equals(givenBy, that.givenBy) &&
                Objects.equals(givenAt, that.givenAt) &&
                Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serverId, user, server, points, givenById, givenBy, givenAt, expiresAt);
    }
}
