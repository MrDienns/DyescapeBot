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

    @Column(name = "type", nullable = false)
    private String type;

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

    private Warning() {
        this(0, 0, "", 0, "", null, 0);
    }

    public Warning(long server, long user, String type, long channel, String reason, Instant end, long punisher) {
        this.id = id;
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

    public long getId() {
        return this.id;
    }

    public long getServer() {
        return this.server;
    }

    public long getUser() {
        return this.user;
    }

    public String getType() {
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
}
