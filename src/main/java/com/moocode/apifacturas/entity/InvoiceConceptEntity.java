package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "factura_conceptos")
public class InvoiceConceptEntity {
    @Id
    @Column(name = "concept_uuid", nullable = false, updatable = false)
    private UUID conceptUuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_uuid", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InvoiceConceptEntity() {}

    public InvoiceConceptEntity(UUID conceptUuid, InvoiceEntity invoice, String description,
                                BigDecimal quantity, BigDecimal unitPrice, BigDecimal amount) {
        this.conceptUuid = conceptUuid;
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public UUID getConceptUuid() { return conceptUuid; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getAmount() { return amount; }
}
