package com.moocode.apifacturas.repository;

import com.moocode.apifacturas.entity.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByPublicNumber(Long publicNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PaymentEntity p join fetch p.status join fetch p.invoice where p.publicNumber = :number")
    Optional<PaymentEntity> findByPublicNumberForUpdate(@Param("number") Long number);

    boolean existsByInvoice_InvoiceUuidAndStatus_Code(UUID invoiceUuid, String statusCode);

    List<PaymentEntity> findAllByInvoice_InvoiceUuidAndStatus_CodeOrderByCreatedAtAsc(UUID invoiceUuid, String statusCode);

    @Query("select coalesce(sum(p.amount), 0) from PaymentEntity p where p.invoice.invoiceUuid = :invoiceUuid and p.status.code = :statusCode")
    BigDecimal sumAmountByInvoiceAndStatus(@Param("invoiceUuid") UUID invoiceUuid, @Param("statusCode") String statusCode);

    long countByStatus_Code(String statusCode);

    @Query("select coalesce(sum(p.amount), 0) from PaymentEntity p where p.status.code = :statusCode")
    BigDecimal sumAmountByStatus(@Param("statusCode") String statusCode);

    @Query("select coalesce(sum(p.amount), 0) from PaymentEntity p where p.invoice.customer.customerUuid = :customerUuid and p.status.code = :statusCode")
    BigDecimal sumAmountByCustomerAndStatus(@Param("customerUuid") UUID customerUuid,
                                            @Param("statusCode") String statusCode);

    List<PaymentEntity> findAllByStatus_CodeAndReviewedAtGreaterThanEqualAndReviewedAtLessThan(
            String statusCode, java.time.Instant start, java.time.Instant end);

    @Query(value = "select p from PaymentEntity p join p.invoice i join i.customer c join c.person person "
            + "where (cast(:email as string) is null or lower(person.email) = lower(cast(:email as string))) "
            + "and (:status is null or p.status.code = :status) "
            + "and (cast(:search as string) is null or cast(p.publicNumber as string) like concat('%', cast(:search as string), '%') "
            + "or cast(i.publicNumber as string) like concat('%', cast(:search as string), '%') "
            + "or lower(coalesce(p.reference, '')) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(person.firstName) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(concat(person.firstName, ' ', person.paternalSurname, ' ', coalesce(person.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(person.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
            + "or lower(person.email) like lower(concat('%', cast(:search as string), '%'))) order by p.createdAt desc",
            countQuery = "select count(p) from PaymentEntity p join p.invoice i join i.customer c join c.person person "
                    + "where (cast(:email as string) is null or lower(person.email) = lower(cast(:email as string))) "
                    + "and (:status is null or p.status.code = :status) "
                    + "and (cast(:search as string) is null or cast(p.publicNumber as string) like concat('%', cast(:search as string), '%') "
                    + "or cast(i.publicNumber as string) like concat('%', cast(:search as string), '%') "
                    + "or lower(coalesce(p.reference, '')) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(person.firstName) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(concat(person.firstName, ' ', person.paternalSurname, ' ', coalesce(person.maternalSurname, ''))) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(person.paternalSurname) like lower(concat('%', cast(:search as string), '%')) "
                    + "or lower(person.email) like lower(concat('%', cast(:search as string), '%'))) ")
    Page<PaymentEntity> searchForAccount(@Param("email") String email, @Param("status") String status,
                                         @Param("search") String search, Pageable pageable);


}