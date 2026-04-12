package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.projection.SalesByPaymentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    @Query("""
                SELECT s
                FROM Sales s
                JOIN FETCH s.customer
                WHERE s.deleteDate IS NULL
                AND s.createdDate BETWEEN :start AND :end
            """)
    Page<Sales> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end,
                                         Pageable pageable);


    @Query("SELECT s FROM Sales s JOIN FETCH s.customer WHERE s.id = :id")
    Optional<Sales> findByIdWithDetails(@Param("id") Long id);


    // Find all active Sales
    @Query("""
            SELECT s
            FROM Sales s
            JOIN FETCH s.customer
            ORDER BY s.id desc
            """)
    Page<Sales> findAllSales(Pageable pageable);

    // Find by id or code if they are active
    @Query("""
                SELECT s
                FROM Sales s
                JOIN FETCH s.customer
                WHERE s.id = :id
            """)
    Page<Sales> findSalesById(
            Long id, Pageable pageable);

    @Query("""
        SELECT s.paymentType AS paymentType,
            COUNT(s) AS totalSales,
            COALESCE(SUM(s.total), 0) AS totalAmount
        FROM Sales s
        WHERE s.status IN ('COMPLETED', 'UPDATED')
          AND s.createdDate BETWEEN :start AND :end
        GROUP BY s.paymentType
    """)
    List<SalesByPaymentProjection> getSalesByPaymentType(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
