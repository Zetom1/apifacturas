package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponseDTO(
        String paymentCode,
        String invoiceCode,
        String status,
        BigDecimal amount,
        String paymentMethod,
        String reference,
        String observations,
        Instant createAt,
        Instant reviewedAt) {}
