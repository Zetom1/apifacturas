package com.moocode.apifacturas.mapper;

import com.moocode.apifacturas.dto.response.InvoiceLineResponseDTO;
import com.moocode.apifacturas.dto.response.InvoiceResponseDTO;
import com.moocode.apifacturas.entity.InvoiceConceptEntity;
import com.moocode.apifacturas.entity.InvoiceEntity;

import java.util.List;

public final class InvoiceMapper {
    private InvoiceMapper() { }

    public static InvoiceResponseDTO toResponse(InvoiceEntity invoice, List<InvoiceConceptEntity> concepts) {
        return new InvoiceResponseDTO(
                "FACT-%06d".formatted(invoice.getPublicNumber()),
                "CLIE-%06d".formatted(invoice.getCustomer().getPublicNumber()),
                invoice.getStatus().getCode(),
                invoice.getSubtotal(), invoice.getTaxAmount(), invoice.getTotal(), invoice.getBalance(),
                invoice.getCurrency(), invoice.getIssueDate(), invoice.getCreatedAt(), invoice.getStampingFolio(),
                invoice.getStampedAt(), invoice.getCancelledAt(),
                concepts.stream().map(line -> new InvoiceLineResponseDTO(
                        line.getDescription(), line.getQuantity(), line.getUnitPrice(), line.getAmount())).toList());
    }
}