package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@IdClass(ActivePunishment.PunishmentKey.class)
@Table(name = "active_punishments")
public class ActivePunishment implements Serializable {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
    @Column(name = "server_id", nullable = false)
    private long server;

    @Id
    @Column(name = "user_id", nullable = false)
    private long user;

    @Id
    @Column(name = "type", nullable = false)
    private PunishmentType type;

    @Id
    @Column(name = "channel", nullable = true)
    private long channel;

    @Column(name = "reason")
    private String reason;

    @Column(name = "end")
    private Instant end;

    @Column(name = "punisher_id", nullable = false)
    private long punisher;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private ActivePunishment() {
        this(0, 0, null, 0, "", null, 0);
    }

    public ActivePunishment(long server, long user, PunishmentType type, long channel, String reason, Instant end, long punisher) {
        this.server = server;
        this.user = user;
        this.type = type;
        this.channel = channel;
        this.reason = reason;
        this.end = end;
        this.punisher = punisher;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public long getServer() {
        return this.server;
    }

    public long getUser() {
        return this.user;
    }

    public PunishmentType getType() {
        return this.type;
    }

    public long getChannel() {
        return this.channel;
    }

    public String getReason() {
        return this.reason;
    }

    public Instant getEnd() {
        return this.end;
    }

    public long getPunisher() {
        return this.punisher;
    }

    // -------------------------------------------- //
    // ID CLASS
    // -------------------------------------------- //

    public static class PunishmentKey implements Serializable {

        private static final long serialVersionUID = 1L;

        public PunishmentKey() {

        }

        public PunishmentKey(long server, long user, PunishmentType type, long channel) {
            this.server = server;
            this.user = user;
            this.type = type;
            this.channel = channel;
        }

        private long server;
        private long user;
        private PunishmentType type;
        private long channel;

        public long getServer() {
            return this.server;
        }

        public long getUser() {
            return this.user;
        }

        public PunishmentType getType() {
            return this.type;
        }

        public long getChannel() {
            return this.channel;
        }
    }
}
