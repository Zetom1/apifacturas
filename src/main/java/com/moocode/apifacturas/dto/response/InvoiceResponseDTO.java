package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record InvoiceResponseDTO(
        String invoiceCode,
        String customerCode,
        String status,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal total,
        BigDecimal balance,
        String currency,
        Instant issueDate,
        Instant createdAt,
        String stampingFolio,
        Instant stampedAt,
        Instant cancelledAt,
        List<InvoiceLineResponseDTO> concepts
) {}
