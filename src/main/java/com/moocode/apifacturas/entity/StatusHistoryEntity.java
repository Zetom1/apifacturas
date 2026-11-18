package com.moocode.apifacturas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historial_estatus")
public class StatusHistoryEntity {
    @Id
    @Column(name = "history_uuid", nullable = false, updatable = false)
    private UUID historyUuid;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(name = "entity_uuid", nullable = false)
    private UUID entityUuid;

    @Column(name = "previous_status", length = 20)
    private String previousStatus;

    @Column(name = "new_status", nullable = false, length = 20)
    private String newStatus;

    @Column(name = "changed_by")
    private UUID changedBy;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected StatusHistoryEntity() {}

    public StatusHistoryEntity(String entityType, UUID entityUuid, String previousStatus, String newStatus, UUID changedBy, String comment) {
        this.entityType = entityType;
        this.entityUuid = entityUuid;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.comment = comment;
    }

    public UUID getHistoryUuid() { return historyUuid; }
    public String getEntityType() { return entityType; }
    public UUID getEntityUuid() { return entityUuid; }
    public String getPreviousStatus() { return previousStatus; }
    public String getNewStatus() { return newStatus; }
    public UUID getChangedBy() { return changedBy; }
    public String getComment() { return comment; }

}
