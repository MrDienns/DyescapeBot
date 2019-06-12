package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@IdClass(Punishment.PunishmentKey.class)
@Table(name = "punishments")
public class Punishment implements Serializable {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "reason")
    private String reason;

    @Column(name = "end")
    private Instant end;

    @Column(name = "punisher")
    private User punisher;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private Punishment() {
        this(null, null, "", "", null, null);
    }

    public Punishment(Server server, User user, String type, String reason, Instant end, User punisher) {
        this.server = server;
        this.user = user;
        this.type = type;
        this.reason = reason;
        this.end = end;
        this.punisher = punisher;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public Server getServer() {
        return this.server;
    }

    public User getUser() {
        return this.user;
    }

    public String getType() {
        return this.type;
    }

    public String getReason() {
        return this.reason;
    }

    public Instant getEnd() {
        return this.end;
    }

    public User getPunisher() {
        return this.punisher;
    }

    // -------------------------------------------- //
    // ID CLASS
    // -------------------------------------------- //

    public class PunishmentKey implements Serializable {

        private static final long serialVersionUID = 1L;

        private long server;
        private long user;
        private String type;

        public long getServer() {
            return this.server;
        }

        public long getUser() {
            return this.user;
        }

        public String getType() {
            return this.type;
        }
    }
}
