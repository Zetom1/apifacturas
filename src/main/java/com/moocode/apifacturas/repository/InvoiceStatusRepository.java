package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.InvoiceStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceStatusRepository extends JpaRepository<InvoiceStatusEntity, UUID> {
    Optional<InvoiceStatusEntity> findByCode(String code);
}
