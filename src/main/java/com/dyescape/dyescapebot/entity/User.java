package com.dyescape.dyescapebot.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    // -------------------------------------------- //
    // COLUMNS
    // -------------------------------------------- //

    @Id
    @Column(name = "user_id")
    private int userId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Punishment> punishments;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private User() {
        this(0, new ArrayList<>());
    }

    public User(int userId, List<Punishment> punishments) {
        this.userId = userId;
        this.punishments = punishments;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public int getUserId() {
        return this.userId;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }
}
