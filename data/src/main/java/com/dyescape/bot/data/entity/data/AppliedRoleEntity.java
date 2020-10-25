package com.dyescape.bot.data.entity.data;

import com.dyescape.bot.data.id.UserServerID;

import javax.persistence.*;

import java.util.Objects;

@Entity
@IdClass(UserServerID.class)
@Table(name = "discord_assigned_role")
public class AppliedRoleEntity {

    @Id
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private String userId;

    @Id
    @Column(name = "role_id", nullable = false, insertable = false, updatable = false)
    private String roleId;

    @Id
    @Column(name = "server_id", nullable = false, insertable = false, updatable = false)
    private String serverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "role_id", referencedColumnName = "id"),
            @JoinColumn(name = "server_id", referencedColumnName = "server_id")
    })
    private RoleEntity role;

    protected AppliedRoleEntity() {

    }

    public AppliedRoleEntity(UserEntity user, RoleEntity role) {
        this.user = user;
        this.userId = user.getId();
        this.role = role;
        this.roleId = role.getId();
        this.serverId = role.getServer().getId();
    }

    public String getUserId() {
        return this.userId;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public UserEntity getUser() {
        return this.user;
    }

    public RoleEntity getRole() {
        return this.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppliedRoleEntity that = (AppliedRoleEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(serverId, that.serverId) &&
                Objects.equals(user, that.user) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId, serverId, user, role);
    }
}
