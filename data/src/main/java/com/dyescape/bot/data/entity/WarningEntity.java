package com.dyescape.bot.data.entity;

import javax.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "discord_warning")
public class WarningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "server_id")
    private String serverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ServerEntity server;

    @Column(name = "points")
    private int points;

    @Column(name = "given_by_id")
    private String givenById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "given_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity givenBy;

    @Column(name = "given_at")
    private Instant givenAt;

    @Column(name = "reason")
    private String reason;

    protected WarningEntity() {

    }

    public WarningEntity(UserEntity user, ServerEntity server, int points, UserEntity givenBy, Instant givenAt,
                         String reason) {
        this.user = user;
        this.userId = user.getId();
        this.server = server;
        this.serverId = server.getId();
        this.givenBy = givenBy;
        this.givenById = givenBy.getId();
        this.points = points;
        this.givenAt = givenAt;
        this.reason = reason;
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
                Objects.equals(givenAt, that.givenAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serverId, user, server, points, givenById, givenBy, givenAt);
    }
}
