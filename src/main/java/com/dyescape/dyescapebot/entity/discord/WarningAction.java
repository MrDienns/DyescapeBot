package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(WarningAction.WarningActionKey.class)
@Table(name = "warning_actions")
public class WarningAction implements Serializable {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
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

    // -------------------------------------------- //
    // ID CLASS
    // -------------------------------------------- //

    public class WarningActionKey implements Serializable {

        private static final long serialVersionUID = 1L;

        private long server;
        private int points;

        public long getServer() {
            return this.server;
        }

        public int getPoints() {
            return this.points;
        }
    }
}
