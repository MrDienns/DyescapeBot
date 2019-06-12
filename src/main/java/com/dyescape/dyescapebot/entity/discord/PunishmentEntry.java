package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "punishment_history")
public class PunishmentEntry implements Serializable {

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

    @Column(name = "duration")
    private long duration;

    @Column(name = "punisher_id", nullable = false)
    private long punisher;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private PunishmentEntry() {
        this(0, 0, "", 0, "", 0, 0);
    }

    public PunishmentEntry(long server, long user, String type, long channel, String reason, long duration, long punisher) {
        this.server = server;
        this.user = user;
        this.type = type;
        this.channel = channel;
        this.reason = reason;
        this.duration = duration;
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

    public long getDuration() {
        return this.duration;
    }

    public long getPunisher() {
        return this.punisher;
    }
}
