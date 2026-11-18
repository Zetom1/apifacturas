package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;

public record CustomerFinancialSummaryDTO(
        String customerCode,
        String customerName,
        long invoiceCount,
        BigDecimal issuedTotal,
        BigDecimal confirmedPayments,
        BigDecimal outstandingBalance
) {}
