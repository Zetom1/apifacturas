package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.InvoiceConceptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceConceptRepository extends JpaRepository<InvoiceConceptEntity,UUID> {
    List<InvoiceConceptEntity> findAllByInvoice_InvoiceUuid(UUID invoiceUuid);
}
