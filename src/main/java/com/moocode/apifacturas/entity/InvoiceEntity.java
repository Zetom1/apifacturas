package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "facturas")
public class InvoiceEntity {
    @Id
    @Column(name = "invoice_uuid", nullable = false, updatable = false)
    private UUID invoiceUuid;

    @Column(name = "public_number", insertable = false, updatable = false, unique = true)
    private Long publicNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_uuid", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_uuid", nullable = false)
    private InvoiceStatusEntity status;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    private String currency = "MXN";
    @Column(name = "issue_date")
    private Instant issueDate;
    @Column(name = "stamped_at")
    private Instant stampedAt;
    @Column(name = "stamping_folio", length = 50, unique = true)
    private String stampingFolio;
    @Column(name = "cancelled_at")
    private Instant cancelledAt;
    @Column(name = "pdf_path", length = 500)
    private String pdfPath;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InvoiceEntity() { }

    public InvoiceEntity(UUID invoiceUuid, CustomerEntity customer, InvoiceStatusEntity status,
                         BigDecimal subtotal, BigDecimal taxAmount, BigDecimal total) {
        this.invoiceUuid = invoiceUuid;
        this.customer = customer;
        this.status = status;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.total = total;
        this.balance = total;
        this.currency = "MXN";
    }

    public void issue(InvoiceStatusEntity emittedStatus) {
        this.status = emittedStatus;
        this.issueDate = Instant.now();
    }

    public void applyConfirmedPayment(BigDecimal amount, InvoiceStatusEntity nextStatus) {
        this.balance = this.balance.subtract(amount).setScale(2);
        this.status = nextStatus;
    }

    public void stamp(InvoiceStatusEntity stampedStatus, String folio, String documentPath) {
        this.status = stampedStatus;
        this.stampingFolio = folio;
        this.stampedAt = Instant.now();
        this.pdfPath = documentPath;
    }

    public void cancel(InvoiceStatusEntity cancelledStatus) {
        this.status = cancelledStatus;
        this.cancelledAt = Instant.now();
    }

    public void updateDraft(CustomerEntity customer, BigDecimal subtotal, BigDecimal taxAmount, BigDecimal total) {
        this.customer = customer;
        this.subtotal = subtotal;
        this.taxAmount = taxAmount;
        this.total = total;
        this.balance = total;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    public UUID getInvoiceUuid() { return invoiceUuid; }
    public Long getPublicNumber() { return publicNumber; }
    public CustomerEntity getCustomer() { return customer; }
    public InvoiceStatusEntity getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public Instant getIssueDate() { return issueDate; }
    public Instant getStampedAt() { return stampedAt; }
    public String getStampingFolio() { return stampingFolio; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getPdfPath() { return pdfPath; }
    public Instant getCreatedAt() { return createdAt; }
}
