package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "warnings")
public class Warning implements Serializable {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "server_id", nullable = false)
    private long server;

    @Column(name = "user_id", nullable = false)
    private long user;

    @Column(name = "channel", nullable = true)
    private long channel;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "reason")
    private String reason;

    @Column(name = "end")
    private Instant end;

    @Column(name = "punisher_id", nullable = false)
    private long punisher;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private Warning() {
        this(0, 0, 0, 0, "", null, 0);
    }

    public Warning(long server, long user, long channel, int points, String reason, Instant end, long punisher) {
        this.server = server;
        this.user = user;
        this.channel = channel;
        this.points = points;
        this.reason = reason;
        this.end = end;
        this.punisher = punisher;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public long getId() {
        return this.id;
    }

    public long getServer() {
        return this.server;
    }

    public long getUser() {
        return this.user;
    }

    public long getChannel() {
        return this.channel;
    }

    public int getPoints() {
        return this.points;
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
}
