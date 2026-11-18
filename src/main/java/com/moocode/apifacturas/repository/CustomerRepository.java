package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.CustomerEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByPublicNumber(Long publicNumber);

    Optional<CustomerEntity> findByPerson_EmailIgnoreCase(String email);

    List<CustomerEntity> findAllByActiveTrueOrderByPublicNumberAsc();

    long countByActiveTrue();

    @Query(value = "select c from CustomerEntity c join c.person p "
            + "where (:active is null or c.active = :active) and "
            + "(cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.maternalSurname) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) "
            + "order by c.publicNumber asc",
            countQuery = "select count(c) from CustomerEntity c join c.person p "
                    + "where (:active is null or c.active = :active) and "
                    + "(cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.maternalSurname) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) ")
    Page<CustomerEntity> search(@Param("search") String search, @Param("active") Boolean active, Pageable pageable);


}
