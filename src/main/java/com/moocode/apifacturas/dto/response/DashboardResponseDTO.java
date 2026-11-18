package com.moocode.apifacturas.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardResponseDTO(
        long activeCustomers,
        Map<String, Long> invoiceByStatus,
        BigDecimal issuedInvoiceTotal,
        BigDecimal confirmedPaymentTotal,
        BigDecimal outstandingBalance,
        long pendingPayments,
        BigDecimal pendingPaymentAmount
) {}
