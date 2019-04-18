package com.dyescape.dyescapebot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "punishments")
public class Punishment {

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "reason")
    private String reason;

    @Column(name = "end")
    private LocalDateTime end;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private Punishment() {
        this(null, null, "", "", null);
    }

    public Punishment(Server server, User user, String type, String reason, LocalDateTime end) {
        this.server = server;

        this.type = type;
        this.reason = reason;
        this.end = end;
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

    public LocalDateTime getEnd() {
        return this.end;
    }
}
