package com.moocode.apifacturas.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notificaciones_outbox")
public class NotificationOutboxEntity {
    @Id
    @Column(name = "notification_uuid", nullable = false, updatable = false)
    private UUID notificationUuid;
    @Column(name = "notification_type", nullable = false, length = 20)
    private String notificationType;
    @Column(name = "recipient", nullable = false, length = 254)
    private String recipient;
    @Column(name = "reference_code", nullable = false, length = 50)
    private String referenceCode;
    @Column(name = "event_status", nullable = false, length = 20)
    private String eventStatus;
    @Column(name = "amount_text", length = 50)
    private String amountText;
    @Column(name = "related_code", length = 50)
    private String relatedCode;
    @Column(name = "message_text", length = 500)
    private String messageText;
    @Column(name = "delivery_status", nullable = false, length = 20)
    private String deliveryStatus = "PENDIENTE";
    @Column(name = "attempts", nullable = false)
    private int attempts;
    @Column(name = "last_error", length = 500)
    private String lastError;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    protected NotificationOutboxEntity() { }

    public NotificationOutboxEntity(String notificationType, String recipient, String referenceCode, String eventStatus, String amountText, String relatedCode, String messageText) {
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.referenceCode = referenceCode;
        this.eventStatus = eventStatus;
        this.amountText = amountText;
        this.relatedCode = relatedCode;
        this.messageText = messageText;
        this.deliveryStatus = "PENDIENTE";
        this.nextAttemptAt = Instant.now();
    }

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = Instant.now(); }

    public void startAttempt() { attempts++; lastError = null; }
    public void markSent() { deliveryStatus = "ENVIADA"; sentAt = Instant.now(); nextAttemptAt = Instant.now(); lastError = null; }
    public void markFailed(String error, boolean finalAttempt, long delaySeconds) {
        lastError = error == null ? "Error de envio sin detalle." : error.substring(0, Math.min(error.length(), 500));
        deliveryStatus = finalAttempt ? "ERROR" : "PENDIENTE";
        nextAttemptAt = finalAttempt ? Instant.now() : Instant.now().plusSeconds(delaySeconds);
    }

    public UUID getNotificationUuid() { return notificationUuid; }
    public String getNotificationType() { return notificationType; }
    public String getRecipient() { return recipient; }
    public String getReferenceCode() { return referenceCode; }
    public String getEventStatus() { return eventStatus; }
    public String getAmountText() { return amountText; }
    public String getRelatedCode() { return relatedCode; }
    public String getMessageText() { return messageText; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public int getAttempts() { return attempts; }
    public String getLastError() { return lastError; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getNextAttemptAt() { return nextAttemptAt; }
}