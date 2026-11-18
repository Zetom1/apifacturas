package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class CustomerEntity {
    @Id
    @Column(name = "customer_uuid", nullable = false, updatable = false)
    private UUID customerUuid;

    @Column(name = "public_number", insertable = false, updatable = false, unique = true)
    private Long publicNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_uuid", nullable = false, unique = true)
    private PersonEntity person;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerEntity() {}

    public CustomerEntity(UUID customerUuid, PersonEntity person){
        this.customerUuid = customerUuid;
        this.person = person;
        this.active = true;
    }

    public void deactivate(){ this.active = false; }
    public void activate(){ this.active = true; }

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate(){this.updatedAt = Instant.now();}

    public UUID getCustomerUuid() {return customerUuid;}
    public Long getPublicNumber() {return publicNumber;}
    public PersonEntity getPerson() {return person;}
    public boolean isActive() {return active;}
    public Instant getCreatedAt() {return createdAt;}
}
