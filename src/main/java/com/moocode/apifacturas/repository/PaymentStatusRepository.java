package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.PaymentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatusEntity, UUID> {
    Optional<PaymentStatusRepository> finByCode(String code);

}
