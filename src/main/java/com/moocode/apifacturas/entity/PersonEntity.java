package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "personas")
public class PersonEntity {

    @Id
    @Column(name = "person_uuid", nullable = false, updatable = false)
    private UUID personUuid;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "paternal_surname", nullable = false, length = 100)
    private String paternalSurname;

    @Column(name = "maternal_surname", length = 100)
    private String maternalSurname;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PersonEntity() {}

    public PersonEntity(UUID personUuid, String firstName, String paternalSurname,
                        String maternalSurname, String email, String phone) {
        this.personUuid = personUuid;
        updateContact(firstName,paternalSurname,maternalSurname,email,phone);
        this.active = true;
        this.verified = false;
    }
    public void updateContact(String firstName, String paternalSurname,
                              String maternalSurname, String email, String phone) {
        this.firstName = firstName;
        this.paternalSurname = paternalSurname;
        this.maternalSurname = maternalSurname;
        this.email = email;
        this.phone = phone;
    }

    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
    public void markVerified() { this.verified = true; }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getPersonUuid() { return personUuid; }
    public String getFirstName() { return firstName; }
    public String getPaternalSurname() { return paternalSurname; }
    public String getMaternalSurname() { return maternalSurname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
    public boolean isVerified() { return verified; }
    public Instant getCreatedAt() { return createdAt; }
}