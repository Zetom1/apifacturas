package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class PaymentEntity {
    @Id
    @Column(name = "payment_uuid", nullable = false, updatable = false)
    private UUID paymentUuid;
    @Column(name = "public_number", insertable = false, updatable = false, unique = true)
    private Long publicNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_uuid", nullable = false)
    private InvoiceEntity invoice;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_uuid", nullable = false)
    private PaymentStatusEntity status;
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;
    @Column(name = "reference", length = 100)
    private String reference;
    @Column(name = "observations", length = 500)
    private String observations;
    @Column(name = "evidence_path", length = 500)
    private String evidencePath;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private UserEntity reviewedBy;
    @Column(name = "reviewed_at")
    private Instant reviewedAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PaymentEntity() { }

    public PaymentEntity(UUID paymentUuid, InvoiceEntity invoice, PaymentStatusEntity status,
                         BigDecimal amount, String paymentMethod, String reference, String observations){
        this.paymentUuid = paymentUuid;
        this.invoice = invoice;
        this.status = status;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.reference = reference;
        this.observations = observations;
    }

    public void review(PaymentStatusEntity status, UserEntity reviewer){
        this.status = status;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
    }

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
    }

    public UUID getPaymentUuid() {return this.paymentUuid;}
    public Long getPublicNumber() {return this.publicNumber;}
    public InvoiceEntity getInvoice() {return this.invoice;}
    public PaymentStatusEntity getStatus() {return this.status;}
    public BigDecimal getAmount() {return this.amount;}
    public String getPaymentMethod() {return this.paymentMethod;}
    public String getReference() {return this.reference;}
    public String getObservations() {return this.observations;}
    public UserEntity getReviewedBy() {return this.reviewedBy;}
    public Instant getReviewedAt() {return this.reviewedAt;}

}