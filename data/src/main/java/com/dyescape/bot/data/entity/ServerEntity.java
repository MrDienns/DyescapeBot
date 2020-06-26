package com.dyescape.bot.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "discord_server")
public class ServerEntity {

    @Id
    private String id;

    @Column(name = "command_prefix")
    private String commandPrefix = "!";

    protected ServerEntity() {

    }

    public ServerEntity(String id, String commandPrefix) {
        this.id = id;
        this.commandPrefix = commandPrefix;
    }

    public String getId() {
        return this.id;
    }

    public String getCommandPrefix() {
        return this.commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerEntity that = (ServerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(commandPrefix, that.commandPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commandPrefix);
    }
}
