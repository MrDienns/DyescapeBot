package com.dyescape.bot.data.entity;

import javax.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "discord_punishment")
public class PunishmentEntity {

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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "action", nullable = false)
    private Action action;

    @Column(name = "given_by_id")
    private String givenById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "given_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity givenBy;

    @Column(name = "given_at")
    private Instant givenAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "reason")
    private String reason;

    @Column(name = "revoked", columnDefinition = "boolean default false")
    private Boolean revoked = false;

    protected PunishmentEntity() {

    }

    public PunishmentEntity(UserEntity user, ServerEntity server, Action action, UserEntity givenBy,
                            Instant givenAt, Instant expiresAt, String reason) {
        this.user = user;
        this.userId = user.getId();
        this.server = server;
        this.serverId = server.getId();
        this.action = action;
        this.givenBy = givenBy;
        this.givenById = givenBy.getId();
        this.givenAt = givenAt;
        this.expiresAt = expiresAt;
        this.reason = reason;
    }

    public Long getId() {
        return this.id;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public String getUserId() {
        return this.userId;
    }

    public ServerEntity getServer() {
        return this.server;
    }

    public String getServerId() {
        return this.serverId;
    }

    public Action getAction() {
        return this.action;
    }

    public UserEntity getGivenBy() {
        return this.givenBy;
    }

    public String getGivenById() {
        return this.givenById;
    }

    public Instant getGivenAt() {
        return this.givenAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public boolean isRevoked() {
        return this.revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunishmentEntity that = (PunishmentEntity) o;
        return revoked == that.revoked &&
                Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(user, that.user) &&
                Objects.equals(server, that.server) &&
                action == that.action &&
                Objects.equals(givenById, that.givenById) &&
                Objects.equals(givenBy, that.givenBy) &&
                Objects.equals(givenAt, that.givenAt) &&
                Objects.equals(expiresAt, that.expiresAt) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, serverId, user, server, action, givenById, givenBy, givenAt, expiresAt, reason, revoked);
    }

    public enum Action {

        WARN,
        MUTE,
        KICK,
        BAN,

        ;
    }
}
