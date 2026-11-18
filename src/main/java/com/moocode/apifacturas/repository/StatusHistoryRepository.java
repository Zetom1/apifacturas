package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.StatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepository extends JpaRepository<StatusHistoryEntity, UUID> {
    List<StatusHistoryEntity> fingAllByEntityUuidOrderByChangedAtAsc(String entityType, UUID entityUuid);
}
