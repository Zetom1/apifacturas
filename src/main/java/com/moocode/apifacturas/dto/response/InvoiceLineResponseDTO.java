package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;

public record InvoiceLineResponseDTO(
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount
) {}
