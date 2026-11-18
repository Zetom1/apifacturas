package com.moocode.apifacturas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "estados_factura")
public class InvoiceStatusEntity {

    @Id
    @Column(name = "status_uuid", nullable = false, updatable = false)
    private UUID statusUuid;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    protected InvoiceStatusEntity() { }

    public UUID getStatusUuid() { return statusUuid; }

    public String getCode() { return code; }

}