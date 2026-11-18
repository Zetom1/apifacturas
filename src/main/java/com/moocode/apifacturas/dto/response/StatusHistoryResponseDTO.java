package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


public record StatusHistoryResponseDTO(
        String previousStatus,
        String newStatus,
        UUID changedBy,
        String comment,
        Instant changedAt) { }