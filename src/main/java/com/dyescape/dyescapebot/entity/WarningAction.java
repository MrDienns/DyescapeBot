package com.dyescape.dyescapebot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "warning_actions")
public class WarningAction {

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Id
    @Column(name = "points")
    private int points;

    @Column(name = "action")
    private String action;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private WarningAction() {
        this(null, 0, "");
    }

    public WarningAction(Server server, int points, String action) {
        this.server = server;
        this.points = points;
        this.action = action;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public Server getServer() {
        return this.server;
    }

    public int getPoints() {
        return this.points;
    }

    public String getAction() {
        return this.action;
    }
}
