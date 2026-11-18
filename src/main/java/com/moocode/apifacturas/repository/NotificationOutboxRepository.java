package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.NotificationOutboxEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutboxEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NotificationOutboxEntity n where n.notificationUuid = :uuid")
    Optional<NotificationOutboxEntity> findByUuidForUpdate(@Param("uuid") UUID uuid);

    List<NotificationOutboxEntity> findTop50ByDeliveryStatusNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            String deliveryStatus, Instant now
    );

    Optional<NotificationOutboxEntity> findByReferenceCodeAndNotificationType(String referenceCode,
                                                                              String notificationType);

    Page<NotificationOutboxEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Collection<Object> findTop50ByDeliveryStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(String pendiente, Instant now);
}
