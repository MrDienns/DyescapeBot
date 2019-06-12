package com.dyescape.dyescapebot.entity.discord;

import javax.persistence.*;
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
    private long userId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Punishment> punishments;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    private User() {
        this(0, new ArrayList<>());
    }

    public User(long userId, List<Punishment> punishments) {
        this.userId = userId;
        this.punishments = punishments;
    }

    // -------------------------------------------- //
    // FIELD ACCESS
    // -------------------------------------------- //

    public long getUserId() {
        return this.userId;
    }

    public List<Punishment> getPunishments() {
        return this.punishments;
    }
}
