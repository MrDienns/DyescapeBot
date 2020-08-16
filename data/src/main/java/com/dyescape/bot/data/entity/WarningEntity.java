package com.dyescape.bot.data.entity;

import javax.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "discord_warning")
public class WarningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "punishment_id")
    private Long punishmentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "punishment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private PunishmentEntity punishment;

    @Column(name = "points")
    private int points;

    protected WarningEntity() {

    }

    public WarningEntity(PunishmentEntity punishment, int points) {
        this.punishment = punishment;
        this.punishmentId =  punishment.getId();
        this.points = points;
    }

    public PunishmentEntity getPunishment() {
        return this.punishment;
    }

    public Long getPunishmentId() {
        return this.punishmentId;
    }

    public int getPoints() {
        return this.points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarningEntity that = (WarningEntity) o;
        return points == that.points &&
                Objects.equals(id, that.id) &&
                Objects.equals(punishmentId, that.punishmentId) &&
                Objects.equals(punishment, that.punishment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, punishmentId, punishment, points);
    }
}
