package com.dyescape.bot.data.entity.data;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(RoleEntity.ID.class)
@Table(name = "discord_role")
public class RoleEntity {

    @Id
    private String id;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "server_id")
    private ServerEntity server;

    protected RoleEntity() {

    }

    public RoleEntity(String id, ServerEntity server) {
        this.id = id;
        this.server = server;
    }

    public String getId() {
        return this.id;
    }

    public ServerEntity getServer() {
        return this.server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity that = (RoleEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(server, that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, server);
    }

    public static class ID implements Serializable {

        private final String id;
        private final ServerEntity server;

        public ID(String role, ServerEntity server) {
            this.id = role;
            this.server = server;
        }

        public String getId() {
            return this.id;
        }

        public ServerEntity getServer() {
            return this.server;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id1 = (ID) o;
            return Objects.equals(id, id1.id) &&
                    Objects.equals(server, id1.server);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, server);
        }
    }
}
