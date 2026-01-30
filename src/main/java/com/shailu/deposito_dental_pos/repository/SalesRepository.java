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

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

    // Find all active Sales
    @Query("""
            SELECT s
            FROM Sales s
            JOIN FETCH s.customer
            WHERE s.deleteDate IS NULL
            """)
    Page<Sales> findByDeleteDateIsNull(Pageable pageable);

    // Find by id or code if they are active
    @Query("""
                SELECT s
                FROM Sales s
                JOIN FETCH s.customer
                WHERE s.deleteDate IS NULL
                AND s.id = :id
            """)
    Page<Sales> findByDeleteDateIsNullAndId(
            Long id, Pageable pageable);


    @Query("""
        SELECT s.paymentType AS paymentType,
            COUNT(s) AS totalSales,
            SUM(s.total) AS totalAmount
        FROM Sales s
        WHERE s.status = 'COMPLETED'
          AND s.createdDate BETWEEN :start AND :end
        GROUP BY s.paymentType
    """)
    List<SalesByPaymentProjection> getSalesByPaymentType(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
