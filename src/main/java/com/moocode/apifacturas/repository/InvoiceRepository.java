package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.InvoiceEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<InvoiceRepository, UUID> {

    Optional<InvoiceEntity> findByPublicNumber(Long publicNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvoiceEntity i join fetch i.status where i.publicNumber = :number")
    Optional<InvoiceEntity> findByPublicNumberForUpdate(@Param("number") Long number);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvoiceEntity i join fetch i.status where i.invoiceUuid = :uuid")
    Optional<InvoiceEntity> findByUuidForUpdate(@Param("uuid") UUID uuid);

    Optional<InvoiceEntity> findByPublicNumberAndCustomer_Person_EmailIgnoreCase(Long publicNumber, String email);

// List<InvoiceEntity> findAllByOrderByCreatedAtDesc();
//List<InvoiceEntity> findAllByCustomer_Person_EmailIgnoreCaseOrderByCreatedAtDesc(String email);

    @Query("select coalesce(sum(i.total), 0) from InvoiceEntity i where i.status.code in ('EMITIDA', 'PAGO_PARCIAL', 'PAGADA', 'TIMBRADA')")
    java.math.BigDecimal sumTotalForIssuedInvoices();

    @Query("select coalesce(sum(i.balance), 0) from InvoiceEntity i where i.status.code in ('EMITIDA', 'PAGO_PARCIAL')")
    java.math.BigDecimal sumOutstandingBalance();

    @Query("select i.status.code, count(i) from InvoiceEntity i group by i.status.code order by i.status.code")
    List<Object[]> countByStatus();

    List<InvoiceEntity> findAllByIssueDateGreaterThanEqualAndIssueDateLessThan(java.time.Instant start, java.time.Instant end);

    @Query("select count(i) from InvoiceEntity i where i.customer.customerUuid = :customerUuid and i.status.code in ('EMITIDA', 'PAGO_PARCIAL', 'PAGADA', 'TIMBRADA')")
    long countFinancialByCustomer(@Param("customerUuid") java.util.UUID customerUuid);

    @Query("select coalesce(sum(i.total), 0) from InvoiceEntity i where i.customer.customerUuid = :customerUuid and i.status.code in ('EMITIDA', 'PAGO_PARCIAL', 'PAGADA', 'TIMBRADA')")
    java.math.BigDecimal sumIssuedByCustomer(@Param("customerUuid") java.util.UUID customerUuid);

    @Query("select coalesce(sum(i.balance), 0) from InvoiceEntity i where i.customer.customerUuid = :customerUuid and i.status.code in ('EMITIDA', 'PAGO_PARCIAL')")
    java.math.BigDecimal sumOutstandingByCustomer(@Param("customerUuid") java.util.UUID customerUuid);

    @Query(value = "select i from InvoiceEntity i join fetch i.customer c join fetch c.person p "
            + "where i.balance > 0 and i.status.code in ('EMITIDA', 'PAGO_PARCIAL') "
            + "and (cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) "
            + "and (:status is null or i.status.code = :status) order by i.issueDate asc",
            countQuery = "select count(i) from InvoiceEntity i join i.customer c join c.person p "
                    + "where i.balance > 0 and i.status.code in ('EMITIDA', 'PAGO_PARCIAL') "
                    + "and (cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) "
                    + "and (:status is null or i.status.code = :status) ")
    Page<InvoiceEntity> findReceivables(@Param("search") String search, @Param("status") String status,
                                        Pageable pageable);

    @Query(value = "select i from InvoiceEntity i join i.customer c join c.person p "
            + "where (cast(:email as string) is null or lower(p.email) = lower(cast(:email as string))) "
            + "and (:status is null or i.status.code = :status) "
            + "and (cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) order by i.createdAt desc",
            countQuery = "select count(i) from InvoiceEntity i join i.customer c join c.person p "
                    + "where (cast(:email as string) is null or lower(p.email) = lower(cast(:email as string))) "
                    + "and (:status is null or i.status.code = :status) "
                    + "and (cast(:search as string) is null or lower(p.firstName) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(concat(p.firstName, ' ', p.paternalSurname, ' ', coalesce(p.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(p.email) like lower(concat('%', cast(:search as string), '%'))) ")
    Page<InvoiceEntity> searchForAccount(@Param("email") String email, @Param("status") String status,
                                         @Param("search") String search, Pageable pageable);

}