package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.projection.ProductOutProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    @Query("""
        SELECT sd
        FROM SaleDetail sd
        JOIN FETCH sd.product
        WHERE sd.sales.id = :saleId and
            sd.product.deleteDate IS NULL
    """)
    List<SaleDetail> findBySaleIdWithProduct(@Param("saleId") Long saleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SaleDetail d WHERE d.sales.id = :saleId")
    void deleteBySaleId(@Param("saleId") Long saleId);

    @Query("""
    SELECT p.code AS code,
           p.name AS name,
           COALESCE(SUM(sd.quantity), 0) AS totalQuantity
    FROM SaleDetail sd
    JOIN sd.product p
    JOIN sd.sales s
    WHERE s.status IN ('COMPLETED', 'UPDATED')
      AND s.createdDate BETWEEN :start AND :end
      AND sd.deleteDate IS NULL
    GROUP BY p.code, p.name
""")
    List<ProductOutProjection> getDailyProductOut(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
