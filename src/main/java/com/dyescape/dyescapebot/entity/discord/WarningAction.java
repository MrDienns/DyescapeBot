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
    private WarningActionType action;

    @Column(name = "end")
    private long duration;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private WarningAction() {
        this(0, 0, null, 0L);
    }

    public WarningAction(long server, int points, WarningActionType action, long duration) {
        this.server = server;
        this.points = points;
        this.action = action;
        this.duration = duration;
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

    public WarningActionType getAction() {
        return this.action;
    }

    public long getDuration() {
        return this.duration;
    }

    // -------------------------------------------- //
    // ENUM
    // -------------------------------------------- //

    public enum WarningActionType {

        MUTE,
        TEMPMUTE,
        KICK,
        BAN,
        TEMPBAN
    }

    // -------------------------------------------- //
    // ID CLASS
    // -------------------------------------------- //

    public static class WarningActionKey implements Serializable {

        private static final long serialVersionUID = 1L;

        public WarningActionKey() {

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
