package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;

public record MonthlyFinancialSummaryDTO(
        int year,
        int month,
        BigDecimal invoiceIssued,
        BigDecimal paymentsConfirmed
) {}
