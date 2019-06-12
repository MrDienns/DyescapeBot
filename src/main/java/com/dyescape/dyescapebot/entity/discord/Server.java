package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servers")
public class Server {

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
    @Column(name = "server_id")
    private long serverId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "server")
    private List<Punishment> punishments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "server")
    private List<WarningAction> warningActions;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private Server() {
        this(0, new ArrayList<>(), new ArrayList<>());
    }

    public Server(long serverId, List<Punishment> punishment, List<WarningAction> warningActions) {
        this.serverId = serverId;
        this.punishments = punishment;
        this.warningActions = warningActions;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public long getServerId() {
        return this.serverId;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public List<WarningAction> getWarningActions() {
        return this.warningActions;
    }
}
