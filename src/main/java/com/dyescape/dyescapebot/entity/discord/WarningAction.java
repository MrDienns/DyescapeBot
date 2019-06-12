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
    @Column(name = "server_id")
    private long server;

    @Id
    @Column(name = "points")
    private int points;

    @Column(name = "action")
    private String action;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private WarningAction() {
        this(0, 0, "");
    }

    public WarningAction(long server, int points, String action) {
        this.server = server;
        this.points = points;
        this.action = action;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public long getServer() {
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

        private WarningActionKey() {

        }

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
