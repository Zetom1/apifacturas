package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<PersonEntity> findByEmailIgnoreCase(String email);

    //que no contenga uuid de persona
    boolean existsByEmailIgnoreCaseAndPersonUuidNot(String email, UUID id);
}
