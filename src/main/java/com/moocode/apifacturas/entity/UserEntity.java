package com.moocode.apifacturas.entity;

import com.moocode.apifacturas.entity.PersonEntity;
import com.moocode.apifacturas.entity.RoleEntity;
import jakarta.persistence.*;

import java.util.UUID;

/* Cuenta de acceso; solo almacena el hash BCrypt de la contraseña. */
@Entity
@Table(name = "usuarios")
public class UserEntity {

    @Id
    @Column(name = "user_uuid", nullable = false, updatable = false)
    private UUID userUuid;

    @Column(name = "public_number", insertable = false, updatable = false, unique = true)
    private Long publicNumber;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "person_uuid", nullable = false, unique = true)
    private PersonEntity person;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_uuid", nullable = false)
    private RoleEntity role;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected UserEntity() {
        // Constructor requerido por JPA.
    }
    public UserEntity(UUID userUuid, PersonEntity person, RoleEntity role, String passwordHash) {
        this.userUuid = userUuid;
        this.person = person;
        this.role = role;
        this.passwordHash = passwordHash;
        this.active = true;
    }

    public UUID getUserUuid() { return userUuid; }
    public Long getPublicNumber() { return publicNumber; }
    public PersonEntity getPerson() { return person; }
    public RoleEntity getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isActive() { return active; }

    public void changePasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}