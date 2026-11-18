package com.moocode.apifacturas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @Column(name = "role_uuid", nullable = false, updatable = false)
    private UUID roleUuid;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected RoleEntity() {}

    public RoleEntity(UUID roleUuid, String code, String name, String description, boolean active) {
        this.roleUuid = roleUuid;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = true;
    }

    public UUID getRoleUuid() { return roleUuid; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }

}
