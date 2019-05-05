package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    private int serverId;

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

    public Server(int serverId, List<Punishment> punishment, List<WarningAction> warningActions) {
        this.serverId = serverId;
        this.punishments = punishment;
        this.warningActions = warningActions;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public int getServerId() {
        return this.serverId;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }

    public List<WarningAction> getWarningActions() {
        return this.warningActions;
    }
}
