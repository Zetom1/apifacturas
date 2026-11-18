package com.moocode.apifacturas.dto.response;

import java.time.Instant;

public record NotificationStatusResponseDTO(
        String notificationType,
        String referenceCode,
        String eventStatus,
        String deliveryStatus,
        int attempts,
        String lastError,
        Instant createdAt,
        Instant sentAt,
        Instant nextAttemptAt
) {}
