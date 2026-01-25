package com.shailu.deposito_dental_pos.repository;

import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    @Query("""
        SELECT sd
        FROM SaleDetail sd
        JOIN FETCH sd.product
        WHERE sd.sales.id = :saleId
    """)
    List<SaleDetail> findBySaleIdWithProduct(@Param("saleId") Long saleId);
}
